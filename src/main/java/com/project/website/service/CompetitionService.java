package com.project.website.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.project.website.Model.Competition;
import com.project.website.Model.Entries;
import com.project.website.Model.Post;
import com.project.website.Model.Recipe;
import com.project.website.Model.User;
import com.project.website.Repository.CompetitionRepository;
import com.project.website.Repository.EntriesRepository;
import com.project.website.Repository.PostRepository;
import com.project.website.Repository.RecipeRepository;
import com.project.website.dto.CompetitionDto;
import com.project.website.dto.Entry;
import com.project.website.dto.LeaderboardDto;
import com.project.website.dto.PostDto;
import com.project.website.dto.RecipeResponse;
import com.project.website.exceptions.RecipeException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CompetitionService {
	private final CompetitionRepository competitionRepository;
	private final EntriesRepository entriesRepository;
	private final AuthService authService;
	private final RecipeRepository recipeRepository;
	private final PostRepository postRepository;
	private final RecipeService recipeService;
	private final PostService postService;

	@Transactional(readOnly = true)
	public Page<CompetitionDto> getAllCompetitions(int page, int elements) {
		return competitionRepository
				.findByApproved(true, PageRequest.of(page, elements, Direction.DESC, "competitionId"))
				.map(this::mapToDtoList);
	}

	@Transactional(readOnly = true)
	public Page<CompetitionDto> getAllActiveCompetitions(int page, int elements) {
		if (authService.isLoggedIn()) {
			return competitionRepository
					.findByActive(true, PageRequest.of(page, elements, Direction.DESC, "competitionId"))
					.map(this::mapToDtoListEntry);
		}
		return competitionRepository.findByActive(true, PageRequest.of(page, elements, Direction.DESC, "competitionId"))
				.map(this::mapToDtoList);
	}

	@Transactional(readOnly = true)
	public Page<LeaderboardDto> leaderboard(Long competitionId, int page, int elements) {
		Competition competition = competitionRepository.findById(competitionId)
				.orElseThrow(() -> new RecipeException("Competition not found"));
		if (competition.isRecipe()) {
			return entriesRepository
					.findByCompetition(competition, PageRequest.of(page, elements, Direction.DESC, "recipe.voteCount"))
					.map(this::mapLeaderboard);
		} else {
			return entriesRepository
					.findByCompetition(competition, PageRequest.of(page, elements, Direction.DESC, "post.likeCount"))
					.map(this::mapLeaderboard);
		}

	}

	@Transactional(readOnly = true)
	public CompetitionDto getCompetition(Long competitionId) {
		return mapToDto(competitionRepository.findById(competitionId)
				.orElseThrow(() -> new RecipeException("Competition not found")));
	}

	@Transactional(readOnly = true)
	public Page<RecipeResponse> recipeParticipants(Long competitionId, int page, int elements) {
		Optional<Competition> competition = competitionRepository.findById(competitionId);
		return entriesRepository
				.findByCompetition(competition.orElseThrow(() -> new RecipeException("competition not found")),
						PageRequest.of(page, elements, Direction.DESC, "id"))
				.map(this::mapRecipe);

	}

	public RecipeResponse mapRecipe(Entries entry) {
		return recipeService.mapEntityDtoList(entry.getRecipe());

	}

	@Transactional(readOnly = true)
	public Page<PostDto> postParticipants(Long competitionId, int page, int elements) {
		Optional<Competition> competition = competitionRepository.findById(competitionId);
		return entriesRepository
				.findByCompetition(competition.orElseThrow(() -> new RecipeException("competition not found")),
						PageRequest.of(page, elements, Direction.DESC, "id"))
				.map(this::mapPost);

	}

	public PostDto mapPost(Entries entry) {
		return postService.mapToDto(entry.getPost());

	}

	@Transactional
	public void entry(Entry entry) {
		if (authService.isLoggedIn()) {
			Competition competition = competitionRepository.findById(entry.getCompetitionId())
					.orElseThrow(() -> new RecipeException(""));
			User user = authService.getCurrentUser();
			if (entriesRepository.findByCompetitionAndUser(competition, user).isPresent()) {
				throw new RecipeException("entry present");
			}

			if (entry.getRecipeId() != null) {
				Recipe recipe = recipeRepository.findById(entry.getRecipeId())
						.orElseThrow(() -> new RecipeException(""));

				entriesRepository.save(mapRecipeEntry(user, recipe, competition));

			} else {
				Post post = postRepository.findById(entry.getPostId()).orElseThrow(() -> new RecipeException(""));
				entriesRepository.save(mapPostEntry(user, post, competition));
			}
		}
	}

	private Entries mapRecipeEntry(User user, Recipe recipe, Competition competition) {
		return Entries.builder().competition(competition).recipe(recipe).user(user).build();
	}

	private Entries mapPostEntry(User user, Post post, Competition competition) {
		return Entries.builder().competition(competition).post(post).user(user).build();
	}

	private CompetitionDto mapToDtoList(Competition competition) {

		return CompetitionDto.builder().about(competition.getAbout()).topic(competition.getTopic())
				.competitionId(competition.getCompetitionId())
				.recipe(competition.isRecipe()).duration(competition.getDuration()).imageS(competition.getImageS())
				.build();
	}

	private CompetitionDto mapToDtoListEntry(Competition competition) {

		return CompetitionDto.builder().about(competition.getAbout()).topic(competition.getTopic())
				.competitionId(competition.getCompetitionId())
				.recipe(competition.isRecipe()).enrolled(entriesRepository
						.findByCompetitionAndUser(competition, authService.getCurrentUser()).isPresent())
				.duration(competition.getDuration()).imageS(competition.getImageS()).build();
	}

	private CompetitionDto mapToDto(Competition competition) {
		return CompetitionDto.builder().about(competition.getAbout()).topic(competition.getTopic())
				.duration(competition.getDuration()).rules(competition.getRules())
				.competitionId(competition.getCompetitionId()).winner(competition.getWinner())
				.winnerUserId(competition.getWinnerUserId()).recipe(competition.isRecipe())
				.imageS(competition.getImageS()).build();
	}

	private LeaderboardDto mapLeaderboard(Entries entry) {
		if (entry.getPost() == null) {
			return LeaderboardDto.builder().userId(entry.getUser().getUserId()).username(entry.getUser().getUsername())
					.count(entry.getPost().getLikeCount()).build();

		} else {
			return LeaderboardDto.builder().userId(entry.getUser().getUserId()).username(entry.getUser().getUsername())
					.count(Long.valueOf(entry.getRecipe().getVoteCount())).build();
		}
	}

}
