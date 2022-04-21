package com.project.website.service;

import java.time.Instant;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.project.website.Model.Entries;
import com.project.website.Model.Post;
import com.project.website.Model.User;
import com.project.website.Repository.CommentRepository;
import com.project.website.Repository.EntriesRepository;
import com.project.website.Repository.ImageRepository;
import com.project.website.Repository.LikeRepository;
import com.project.website.Repository.PostRepository;
import com.project.website.Repository.UserRepository;
import com.project.website.dto.PostDto;
import com.project.website.exceptions.RecipeException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PostService {
	private final PostRepository postRepository;
	private final AuthService authService;
	private final UserRepository userRepository;
	private final CommentRepository commentRepository;
	private final ImageRepository imageRepository;
	private final LikeRepository likeRepository;
	private final EntriesRepository entriesRepository;
	private final RecipeService recipeService;

	@Transactional
	public void save(String text, String url, MultipartFile[] images) {
		postRepository.save(map(text, url, images, authService.getCurrentUser()));
	}

	private Post map(String text, String url, MultipartFile[] images, User currentUser) {

		return Post.builder().createdDate(Instant.now()).text(text).user(authService.getCurrentUser())
				.videoCode(RecipeService.VideoCode(url)).approved(false)
				.likeCount(Long.valueOf(0)).image(recipeService.saveImages(images, 640, 480)).build();

	}

	public PostDto mapToDto(Post post) {
		return PostDto.builder().createdDate(post.getCreatedDate()).text(post.getText())
				.url(post.getVideoCode().equals("") ? null : "https://www.youtube.com/embed/" + post.getVideoCode())
				.userName(post.getUser().getUsername())
				.postId(post.getPostId())
				.duration(RecipeService.getDuration(post.getCreatedDate())).userId(post.getUser().getUserId())
				.readonly(readonly(post)).currUser(curUser(post)).likeCount(RecipeService.format(post.getLikeCount()))
				.isLiked(isLiked(post)).commentCount(RecipeService.format(commentCount(post)))
				.media(post.getVideoCode().equals("") && post.getImage().length() == 0).image(imageArr(post.getImage()))
				.profileImage(post.getUser().getProfileImageS()).build();
	}

	private boolean curUser(Post post) {
		if (authService.isLoggedIn()) {
			return authService.getCurrentUser().getUserId() == post.getUser().getUserId();
		}
		return false;
	}

	private String[] imageArr(String str) {
		if (str == null | str.length() == 0) {
			return null;
		} else {
			return str.substring(0, str.length() - 1).split("/");

		}

	}

	@Transactional(readOnly = true)
	Integer commentCount(Post post) {
		return ((Long) commentRepository.findByPost(post, PageRequest.of(0, 1)).getTotalElements()).intValue();
	}

	private boolean readonly(Post post) {
		if (authService.isLoggedIn()) {
			return !(authService.getCurrentUser().getUserId() == post.getUser().getUserId());
		}
		return true;
	}

	@Transactional
	private boolean isLiked(Post post) {
		if (authService.isLoggedIn()) {
			return likeRepository.findByPostAndUser(post, authService.getCurrentUser()).isPresent();
		}
		return false;
	}

	@Transactional(readOnly = true)

	@Cacheable(value = "ten-minute-cache", key = "'TopPostCache'+#page+#elements")
	public Page<PostDto> getAllPost(Integer page, int elements) {
		return postRepository.findByApproved(PageRequest.of(page, elements, Direction.DESC, "createdDate"), true)
				.map(this::mapToDto);
	}

	@Transactional(readOnly = true)
	public PostDto getPost(Long postId) {
		return mapToDto(postRepository.findById(postId).orElseThrow(() -> new RecipeException("Post not found")));
	}

	@Transactional(readOnly = true)
	public Page<PostDto> getPostByUserId(Long userId, Integer page, int elements) {
		User user = userRepository.findById(userId).orElseThrow(() -> new RecipeException("user not found"));
		return postRepository
				.findByUserAndApproved(user, true, (PageRequest.of(page, elements, Direction.DESC, "createdDate")))
				.map(this::mapToDto);
	}

	@Transactional
	public void delete(Long postId) {
		if (authService.isLoggedIn()) {
			User user = authService.getCurrentUser();
			Optional<Post> post = postRepository.findById(postId);
			if (post.get().getUser().getUserId() != user.getUserId()) {
				throw new RecipeException("someone else trying to delete");
			}
			if (post.isPresent()) {
				likeRepository.deleteByPost(post.orElseThrow(() -> new RecipeException("")));
				entriesRepository.deleteBypost(post.orElseThrow(() -> new RecipeException("")));

				commentRepository.deleteByPostAndUser(post.orElseThrow(() -> new RecipeException("")),
						authService.getCurrentUser());
				postRepository.deleteByPostIdAndUser(postId, authService.getCurrentUser());

			}

		}
	}

}
