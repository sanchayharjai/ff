package com.project.website.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.project.website.Model.Images;
import com.project.website.Model.Post;
import com.project.website.Model.Recipe;
import com.project.website.Model.User;
import com.project.website.Repository.PostRepository;
import com.project.website.Repository.RecipeRepository;
import com.project.website.Repository.UserRepository;
import com.project.website.dto.RecipeResponse;
import com.project.website.service.RecipeService;
//
//import com.google.cloud.storage.BlobId;
//import com.google.cloud.storage.BlobInfo;
//import com.google.cloud.storage.Storage;
//import com.google.cloud.storage.StorageException;
//import com.google.cloud.storage.StorageOptions;

import lombok.AllArgsConstructor;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("/api/recipe")
@AllArgsConstructor
public class RecipeController {
	private final RecipeService recipeService;
//	private final Storage storage;
	private final RecipeRepository recipeRepository;
	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final SecureRandom rand = new SecureRandom();

	@PostMapping
	public ResponseEntity<Void> createRecipe(@RequestParam(required = false) String recipeName,
			@RequestParam(required = false) String url, @RequestParam(required = false) String steps,
			@RequestParam(required = false) MultipartFile[] files, @RequestParam(required = false) String category,
			@RequestParam(required = false) MultipartFile thumbnail, @RequestParam(required = false) Long video,
			@RequestParam(required = false) String nutritionData, @RequestParam(required = false) String ingredients,
			@RequestParam(required = false) String description, @RequestParam(required = false) String timeTaken,
			@RequestParam(required = false) String servings) throws IOException {
		recipeService.save(recipeName, url, steps, files, category, thumbnail, nutritionData, ingredients, description,
				timeTaken, servings(servings), video == -1 ? null : Long.valueOf(video));
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@PostMapping("/video")
	public ResponseEntity<Void> Video(@RequestParam MultipartFile video, @RequestParam Long id) throws IOException {
//		recipeService.saveVideo(video, id);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@GetMapping("/update")
	public ResponseEntity<Void> update() throws IOException {
		recipeService.updateBalance();
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@GetMapping("/")
	public ResponseEntity<Page<RecipeResponse>> getAllRecipes(@RequestParam int page, @RequestParam int elements) {
		return status(HttpStatus.OK).body(recipeService.getAllRecipes(page, elements));

	}

	@GetMapping("/similar")
	public ResponseEntity<List<RecipeResponse>> getSimilar(@RequestParam String category) {
		return status(HttpStatus.OK).body(recipeService.getSimilar(category));

	}

	@GetMapping("/{id}")
	public ResponseEntity<RecipeResponse> getRecipe(@PathVariable Long id) {
		return status(HttpStatus.OK).body(recipeService.getRecipe(id));
	}

	@GetMapping("/by-user/{name}")
	public ResponseEntity<Page<RecipeResponse>> getRecipeByUsername(@PathVariable String name, @RequestParam int page,
			@RequestParam int elements) {
		return status(HttpStatus.OK).body(recipeService.getRecipesByUsernameAndApproved(name, page, elements));
	}

	@GetMapping("/by-userid/{userId}")
	public ResponseEntity<Page<RecipeResponse>> getRecipeByUserId(@PathVariable Long userId, @RequestParam int page,
			@RequestParam int elements) {
		return status(HttpStatus.OK).body(recipeService.getRecipesByUserIdAndApproved(userId, page, elements));
	}

	@GetMapping("/by-category/{category}")
	public ResponseEntity<Page<RecipeResponse>> getRecipeByCategory(@PathVariable String category,
			@RequestParam int page, @RequestParam int elements) {
		return status(HttpStatus.OK).body(recipeService.getRecipesByCategoryAndApproved(category, page, elements));
	}

	@GetMapping("/search/{search}")
	public ResponseEntity<Page<RecipeResponse>> searchRecipe(@PathVariable String search, @RequestParam int page,
			@RequestParam int elements) {
		return status(HttpStatus.OK).body(recipeService.searchRecipe(search, page, elements));

	}

	@GetMapping("/top")
	public ResponseEntity<Page<RecipeResponse>> getTopRecipe(@RequestParam int page, @RequestParam int elements) {
		return status(HttpStatus.OK).body(recipeService.getTopRecipe(page, elements));
	}

	@GetMapping("/favourites")
	public ResponseEntity<Page<RecipeResponse>> getFavourites(@RequestParam int page, @RequestParam int elements) {
		return status(HttpStatus.OK).body(recipeService.getFavourites(page, elements));
	}

	@GetMapping("/random")
	public ResponseEntity<Page<RecipeResponse>> getRandom(@RequestParam int page, @RequestParam int elements) {
		return status(HttpStatus.OK).body(recipeService.getRandom(page, elements));
	}

	@GetMapping("/follow")
	public ResponseEntity<Page<RecipeResponse>> getRecipeByFollowed(@RequestParam int page,
			@RequestParam int elements) {
		return status(HttpStatus.OK).body(recipeService.getRecipeByFollowed(page, elements));
	}

	private Integer servings(String servings) {
		try{
			return Integer.valueOf(servings);
		}catch(NumberFormatException e) {
			return 0;
		}
	}
//
//	@GetMapping("/test")
//	@Transactional(isolation = Isolation.SERIALIZABLE)
//	public ResponseEntity<Void> test() throws IOException {
//		List<Recipe> list = recipeRepository.findAll();
//
//		for (Recipe r : list) {
//			String idf = "";
//			Long thumbnailL = Long.valueOf((this.rand.nextInt() & Integer.MAX_VALUE));
//			r.setThumbnailL(String.valueOf(thumbnailL));
//			BlobInfo blobInfo1 = storage.create(BlobInfo.newBuilder("foodym", thumbnailL + ".png")
////                  .setAcl(new ArrayList<>(Arrays.asList(Acl.of(User.ofAllUsers(), Role.READER))))
//					.build(), r.getThumbnailLarge()
//
//			);
//			Long thumbnailS = Long.valueOf((this.rand.nextInt() & Integer.MAX_VALUE));
//			r.setThumbnailS(String.valueOf(thumbnailS));
//			BlobInfo blobInfo2 = storage.create(BlobInfo.newBuilder("foodym", thumbnailS + ".png")
////                  .setAcl(new ArrayList<>(Arrays.asList(Acl.of(User.ofAllUsers(), Role.READER))))
//					.build(), r.getThumbnailSmall()
//
//			);
//
//			for (int i = 0; i < r.getImages().size(); i++) {
//				Long id = Long.valueOf((this.rand.nextInt() & Integer.MAX_VALUE));
//
//				byte[] image = r.getImages().get(i).getImage();
//				if (image.length != 0) {
//					idf = idf + id + "/";
//
//					BlobInfo blobInfo = storage.create(BlobInfo.newBuilder("foodym", id + ".png")
////                                  .setAcl(new ArrayList<>(Arrays.asList(Acl.of(User.ofAllUsers(), Role.READER))))
//							.build(), image
//
//					);
//				}
//				r.setImage(idf);
//				recipeRepository.save(r);
//			}
//		}
//		List<Post> posts = postRepository.findAll();
//		for (Post p : posts) {
//			String idf = "";
//			for (int i = 0; i < p.getImages().size(); i++) {
//				Long id = Long.valueOf((this.rand.nextInt() & Integer.MAX_VALUE));
//				byte[] image = p.getImages().get(i).getImage();
//				if (image.length != 0) {
//					idf = idf + id + "/";
//
//					BlobInfo blobInfo = storage.create(BlobInfo.newBuilder("foodym", id + ".png")
////                                  .setAcl(new ArrayList<>(Arrays.asList(Acl.of(User.ofAllUsers(), Role.READER))))
//							.build(), image
//
//					);
//				}
//				p.setImage(idf);
//				postRepository.save(p);
//			}
//
//		}
//		List<User> users = userRepository.findAll();
//		for (User u : users) {
//			if (u.getProfileImageLarge() != null) {
//				Long l = Long.valueOf((this.rand.nextInt() & Integer.MAX_VALUE));
//				u.setProfileImageL(String.valueOf(l));
//				BlobInfo blobInfo1 = storage.create(BlobInfo.newBuilder("foodym", l + ".png")
////                  .setAcl(new ArrayList<>(Arrays.asList(Acl.of(User.ofAllUsers(), Role.READER))))
//						.build(), u.getProfileImageLarge()
//
//				);
//				Long l1 = Long.valueOf((this.rand.nextInt() & Integer.MAX_VALUE));
//				u.setProfileImageS(String.valueOf(l1));
//				BlobInfo blobInfo2 = storage.create(BlobInfo.newBuilder("foodym", l1 + ".png")
////                  .setAcl(new ArrayList<>(Arrays.asList(Acl.of(User.ofAllUsers(), Role.READER))))
//						.build(), u.getProfileImageSmall()
//
//				);
//
//				userRepository.save(u);
//			}
//
//		}
//
//		return new ResponseEntity<>(HttpStatus.CREATED);
//	}

}
