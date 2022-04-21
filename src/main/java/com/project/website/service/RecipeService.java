package com.project.website.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.Random;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.filters.RemoteIpFilter;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import static java.util.stream.Collectors.*;
import com.github.marlonlom.utilities.timeago.TimeAgo;
//import com.google.cloud.WriteChannel;
//import com.google.cloud.storage.BlobId;
//import com.google.cloud.storage.BlobInfo;
//import com.google.cloud.storage.Storage;
import com.project.website.Model.Favourite;
import com.project.website.Model.Images;
import com.project.website.Model.Ip;
import com.project.website.Model.Rating;
import com.project.website.Model.Recipe;
import com.project.website.Model.User;
import com.project.website.Model.Vote;
import com.project.website.Model.VoteType;
import com.project.website.Repository.CommentRepository;
import com.project.website.Repository.FavouriteRepository;
import com.project.website.Repository.FollowerRepository;
import com.project.website.Repository.IpRepository;
import com.project.website.Repository.RatingRepository;
import com.project.website.Repository.RecipeRepository;
import com.project.website.Repository.UserRepository;
import com.project.website.Repository.VoteRepository;
import com.project.website.dto.RecipeResponse;
import com.project.website.exceptions.RecipeException;
import com.project.website.exceptions.RecipeNotFoundException;
import lombok.AllArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;

@Service
@AllArgsConstructor

public class RecipeService {
	private final RecipeRepository recipeRepository;
	private final AuthService authService;
	private final CommentRepository commentRepository;
	private final UserRepository userRepository;
	private final FavouriteRepository favouriteRepository;
	private final VoteRepository voteRepository;
	private final RatingRepository ratingRepository;
	private final FollowerRepository followerRepository;
	private final SecureRandom rand = new SecureRandom();
//	private final Storage storage;
	private final HttpServletRequest request;
	private final IpRepository ipRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional(isolation = Isolation.SERIALIZABLE)
	public void save(String recipeName, String url, String steps, MultipartFile[] files, String category,
			MultipartFile thumbnail, String nutritionData, String ingredients, String description, String timeTaken,
			Integer servings, Long video) throws IOException {
		User currentUser = authService.getCurrentUser();

		recipeRepository.save(map(recipeName, url, steps, currentUser, files, category, thumbnail, nutritionData,
				ingredients, description, timeTaken, servings, video));
		if (recipeRepository.findByUser(currentUser).size() == 1 && currentUser.getRewards() == false) {
			currentUser.setBalance(currentUser.getBalance() + 100);
			currentUser.setRewards(true);
			currentUser.setTEarnings(currentUser.getTEarnings() + 100);
			userRepository.save(currentUser);
		}
	}

	public RecipeResponse mapEntityDto(Recipe recipe) {
		return RecipeResponse.builder().id(recipe.getRecipeId()).recipeName(recipe.getRecipeName())
				.steps(recipe.getSteps())
				.url(recipe.getUrl().equals("") ? null
						: "https://www.youtube.com/embed/" + recipe.getUrl() + "?ps=docs&controls=1")
				.nutritionData(recipe.getNutritionData()).category(recipe.getCategory())
				.createdDate(recipe.getCreatedDate()).description(recipe.getDescription())
				.ingredients(recipe.getIngredients()).duration(getDuration(recipe.getCreatedDate()))
				.timeTaken(recipe.getTimeTaken().equals("null") ? "na" : recipe.getTimeTaken())
				.voteCount(recipe.getVoteCount()).userName(recipe.getUser().getUsername())
				.rating(ratingCalculate(recipe.getRatingTotal(), recipe.getRatingCount()))
				.servings(recipe.getServings()).userId(recipe.getUser().getUserId()).upVote(isRecipeUpVoted(recipe))
				.downVote(isRecipeDownVoted(recipe)).favourite(isFavourite(recipe)).rated(isRated(recipe))
				.ratingByUser(ratingByUser(recipe)).isFollowed(isFollowed(recipe.getUser()))
				.currUser(currUser(recipe.getUser())).rateCountn(recipe.getRatingCount())
				.rateTotal(recipe.getRatingTotal()).thumbnailL(recipe.getThumbnailL())
				.profileImage(recipe.getUser().getProfileImageS()).image(imageArr(recipe.getImage()))
				.views(recipe.getViews()).videoId(recipe.getVideo() == null ? null : recipe.getVideo().toString())
				.build();

	}

	public RecipeResponse mapFavouriteRecipe(Favourite favourite) {
		return mapEntityDtoList(favourite.getRecipe());
	}

	Recipe map(String recipeName, String url, String steps, User user, MultipartFile[] files, String category,
			MultipartFile thumbnail, String nutritionData, String ingredients, String description, String timeTaken,
			Integer servings, Long video) throws IOException {
System.out.println(video+ "   " +servings);
		return Recipe.builder().recipeName(recipeName).ratingTotal(Long.valueOf(0)).ratingCount(Long.valueOf(0))
				.url(VideoCode(url)).steps(steps).user(user).category(category)

				.nutritionData(nutritionData).ingredients(ingredients).description(description)
				.createdDate(Instant.now()).timeTaken(timeTaken).voteCount(Integer.valueOf(0)).servings(servings)
				.image(saveImages(files, 640, 480)).thumbnailL(saveImage(thumbnail, 640, 480))
				.thumbnailS(saveImage(thumbnail, 440, 330)).video(video).views(Long.valueOf(0)).build();

	}

	public RecipeResponse mapEntityDtoList(Recipe recipe) {

		return RecipeResponse.builder().id(recipe.getRecipeId()).recipeName(recipe.getRecipeName())
				.commentCount(format(commentCount(recipe))).category(recipe.getCategory())
				.createdDate(recipe.getCreatedDate()).description(recipe.getDescription())
				.duration(getDuration(recipe.getCreatedDate())).userName(recipe.getUser().getUsername())
				.rating(ratingCalculate(recipe.getRatingTotal(), recipe.getRatingCount()))
				.rateCount(format(recipe.getRatingCount())).userId(recipe.getUser().getUserId())
				.favourite(isFavourite(recipe)).thumbnailS(recipe.getThumbnailS())
				.profileImage(recipe.getUser().getProfileImageS()).views(recipe.getViews()).build();

	}

	private String[] imageArr(String str) {
		if (str.length() == 0 | str == null) {
			return null;
		} else {
			return str.substring(0, str.length() - 1).split("/");

		}

	}

	private static final NavigableMap<Long, String> suffixes = new TreeMap<>();
	static {
		suffixes.put(1_000L, "k");
		suffixes.put(1_000_000L, "M");
		suffixes.put(1_000_000_000L, "G");
		suffixes.put(1_000_000_000_000L, "T");
		suffixes.put(1_000_000_000_000_000L, "P");
		suffixes.put(1_000_000_000_000_000_000L, "E");
	}

	public static String format(long value) {
		// Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
		if (value == Long.MIN_VALUE)
			return format(Long.MIN_VALUE + 1);
		if (value < 0)
			return "-" + format(-value);
		if (value < 1000)
			return Long.toString(value); // deal with easy case

		Entry<Long, String> e = suffixes.floorEntry(value);
		Long divideBy = e.getKey();
		String suffix = e.getValue();

		long truncated = value / (divideBy / 10); // the number part of the output times 10
		boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
		return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
	}

	static String VideoCode(String url) {
		String videoCode = url;
		for (int i = 0; i < url.length(); i++) {
			char c = url.charAt(i);
			if (c == '=') {
				videoCode = url.substring(i + 1, i + 12);

				break;
			}
		}
		return videoCode;
	}

	public Float ratingCalculate(Long ratingTotal, Long ratingCount) {
		return Float.valueOf(ratingTotal) / Float.valueOf(ratingCount);

	}

	public String saveImage(MultipartFile file, int width, int height) {
		Long id = Long.valueOf((rand.nextInt() & Integer.MAX_VALUE))
				+ Long.valueOf((rand.nextInt() & Integer.MAX_VALUE))
				+ Long.valueOf((rand.nextInt() & Integer.MAX_VALUE));

		saveImageUtil(file, width, height, id);

		return String.valueOf(id);
	}

	@Async
	public void saveImageUtil(MultipartFile file, int width, int height, Long id) {
		byte[] im = null;
		if (file.getSize() > 2500000) {
			im = thumbnalateQ(file, width, height, 0.75);
			System.out.println(1);
		} else if (file.getSize() > 2000000) {
			im = thumbnalateQ(file, width, height, 0.79);
			System.out.println(2);
		} else if (file.getSize() > 1500000) {
			im = thumbnalateQ(file, width, height, 0.83);
			System.out.println(3);
		} else if (file.getSize() > 1000000) {
			im = thumbnalateQ(file, width, height, 0.87);
			System.out.println(4);
		} else if (file.getSize() > 800000) {
			im = thumbnalateQ(file, width, height, 0.91);
			System.out.println(5);
		} else if (file.getSize() > 600000) {
			im = thumbnalateQ(file, width, height, 0.94);
			System.out.println(6);
		} else if (file.getSize() > 400000) {
			im = thumbnalateQ(file, width, height, 0.97);
			System.out.println(7);
		} else {
			im = thumbnalate(file, width, height);
			System.out.println(8);

		}

//		BlobInfo blobInfo = storage.create(BlobInfo.newBuilder("foodys", id + ".png")
////                              .setAcl(new ArrayList<>(Arrays.asList(Acl.of(User.ofAllUsers(), Role.READER))))
//				.build(), im
//
//		);

	}

	public String saveImages(MultipartFile[] files, int width, int height) {
		String idf = "";

		for (MultipartFile file : files) {

			Long id = Long.valueOf((rand.nextInt() & Integer.MAX_VALUE))
					+ Long.valueOf((rand.nextInt() & Integer.MAX_VALUE))
					+ Long.valueOf((rand.nextInt() & Integer.MAX_VALUE));
			idf = idf + id + "/";
			byte[] im = null;
			saveImageUtil(file, width, height, id);
		}
		return idf;

	}

	public static List<Images> imagesAsList(MultipartFile[] files) {
		List<Images> list = new ArrayList<>();
		for (int i = 0; i < files.length; i++) {
			Images image = new Images();
			image.setImage(RecipeService.thumbnalate(files[i], 680, 480));

			list.add(image);
		}
		return list;
	}

	public static byte[] thumbnalate(MultipartFile file, int width, int height) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			Thumbnails.of(file.getInputStream()).size(width, height).keepAspectRatio(true).toOutputStream(outputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		byte[] b = outputStream.toByteArray();
		try {
			outputStream.flush();
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return b;

	}

	public static byte[] thumbnalateQ(MultipartFile file, int width, int height, double quality) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			Thumbnails.of(file.getInputStream()).size(width, height).keepAspectRatio(true).outputQuality(quality)
					.toOutputStream(outputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		byte[] b = outputStream.toByteArray();
		try {
			outputStream.flush();
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return b;

	}

	public static byte[] thumbnalate1(MultipartFile file, int width, int height) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			Thumbnails.of(file.getInputStream()).size(width, height).outputFormat("png").keepAspectRatio(true)
					.toOutputStream(outputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		return outputStream.toByteArray();

	}

	public static byte[][] listAsImages(List<Images> list) {
		byte[][] images = new byte[list.size()][];
		for (int i = 0; i < list.size(); i++) {
			images[i] = list.get(i).getImage();

		}

		return images;
	}

	@Transactional(readOnly = true)
	Integer commentCount(Recipe recipe) {
		return ((Long) commentRepository.findByRecipe(recipe, PageRequest.of(0, 1)).getTotalElements()).intValue();
	}

	public static String getDuration(Instant createdDate) {
		return TimeAgo.using(createdDate.toEpochMilli());
	}

	boolean isRecipeUpVoted(Recipe recipe) {
		return checkVoteType(recipe, VoteType.UPVOTE);
	}

	boolean isRecipeDownVoted(Recipe recipe) {
		return checkVoteType(recipe, VoteType.DOWNVOTE);
	}

	boolean currUser(User user) {
		if (authService.isLoggedIn()) {
			return authService.getCurrentUser().getUserId() == user.getUserId();
		}
		return false;
	}

	@Transactional(readOnly = true)
	private boolean checkVoteType(Recipe recipe, VoteType voteType) {
		if (authService.isLoggedIn()) {
			Optional<Vote> voteForRecipeByUser = voteRepository.findTopByRecipeAndUserOrderByVoteIdDesc(recipe,
					authService.getCurrentUser());
			return voteForRecipeByUser.filter(vote -> vote.getVoteType().equals(voteType)).isPresent();
		}
		return false;
	}

	@Transactional(readOnly = true)
	private boolean isFavourite(Recipe recipe) {
		if (authService.isLoggedIn()) {

			return favouriteRepository.findByRecipeAndUser(recipe, authService.getCurrentUser()).isPresent();
		}
		return false;
	}

	@Transactional(readOnly = true)
	public boolean isFollowed(User user) {
		if (authService.isLoggedIn()) {

			return followerRepository.findByUserAndFollowedUserId(authService.getCurrentUser(), user.getUserId())
					.isPresent();
		}
		return false;
	}

	@Transactional(readOnly = true)
	private boolean isRated(Recipe recipe) {
		if (authService.isLoggedIn()) {
			return ratingRepository.findByRecipeAndUser(recipe, authService.getCurrentUser()).isPresent();
		}
		return false;
	}

	@Transactional(readOnly = true)
	private Integer ratingByUser(Recipe recipe) {
		if (authService.isLoggedIn()) {
			Optional<Rating> rating = ratingRepository.findByRecipeAndUser(recipe, authService.getCurrentUser());
			if (rating.isPresent()) {
				return rating.get().getRateCount();
			} else {
				return -1;
			}
		}
		return -1;
	}

	@Transactional
	public RecipeResponse getRecipe(Long id) {

		Recipe recipe = recipeRepository.findById(id).orElseThrow(() -> new RecipeNotFoundException(id.toString()));
		saveIp(recipe);

		return mapEntityDto(recipe);

	}

	@Transactional(isolation = Isolation.SERIALIZABLE)
	@Async
	private void saveIp(Recipe recipe) {
		boolean flag = false;
		String ipUser = request.getRemoteAddr();
		for (Ip ip : recipe.getIp()) {
			if (passwordEncoder.matches(ipUser, ip.getIp())) {
				flag = true;
			}
		}
		if (flag == false) {
			ipRepository.save(Ip.builder().recipe(recipe).ip(passwordEncoder.encode(ipUser)).createdDate(Instant.now())
					.counted(false).build());
		}
		recipe.setViews(recipe.getViews() + 1);
		User user = recipe.getUser();
		user.setTViews(user.getTViews() + 1);
		userRepository.save(user);
		recipeRepository.save(recipe);
	}

	@Transactional(readOnly = true)
	public Page<RecipeResponse> getAllRecipes(int page, int elements) {

		return recipeRepository.findByApproved(true, PageRequest.of(page, elements, Direction.DESC, "createdDate"))
				.map(this::mapEntityDtoList);
	}

	@Transactional(readOnly = true)
	public Page<RecipeResponse> getRecipesByUsernameAndApproved(String username, int page, int elements) {
		User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
		return recipeRepository
				.findByUserAndApproved(user, true, PageRequest.of(page, elements, Direction.DESC, "createdDate"))
				.map(this::mapEntityDtoList);

	}

	@Transactional(readOnly = true)
	public Page<RecipeResponse> getRecipesByUserIdAndApproved(Long userId, int page, int elements) {
		User user = userRepository.findById(userId).orElseThrow(() -> new RecipeException("user not found"));
		return recipeRepository
				.findByUserAndApproved(user, true, PageRequest.of(page, elements, Direction.DESC, "createdDate"))
				.map(this::mapEntityDtoList);

	}

	@Transactional(readOnly = true)
	@Cacheable(value = "ten-minute-cache", key = "'CategoryCache'+#category+#page+#elements")
	public Page<RecipeResponse> getRecipesByCategoryAndApproved(String category, int page, int elements) {

		return recipeRepository.findByCategoryAndApproved(category, true,
				PageRequest.of(page, elements, Direction.DESC, "createdDate")).map(this::mapEntityDtoList);

	}

	@Transactional(readOnly = true)
	public Page<RecipeResponse> searchRecipe(String search, int page, int elements) {
		return recipeRepository
				.findByRecipeNameAndApproved(search, true, PageRequest.of(page, elements, Direction.DESC, "voteCount"))
				.map(this::mapEntityDtoList);
	}

	@Transactional(readOnly = true)
	public Page<RecipeResponse> getFavourites(int page, int elements) {
		return favouriteRepository.findByUser(authService.getCurrentUser(), PageRequest.of(page, elements))
				.map(this::mapFavouriteRecipe);
	}

	@Transactional(readOnly = true)
	@Cacheable(value = "ten-minute-cache", key = "'TopCache'+#page+#elements")
	public Page<RecipeResponse> getTopRecipe(int page, int elements) {

		return recipeRepository
				.findByVoteCountAndApproved(true, PageRequest.of(page, elements, Direction.DESC, "voteCount"))
				.map(this::mapEntityDtoList);
	}

	@Transactional(readOnly = true)
	@Cacheable(value = "thirty-second-cache", key = "'RandomCache'+#page+#elements")
	public Page<RecipeResponse> getRandom(int page, int elements) {
		return recipeRepository.findRandomWithoutCatAndLimit(true, PageRequest.of(page, elements))
				.map(this::mapEntityDtoList);
	}

	@Transactional(readOnly = true)
	public Page<RecipeResponse> getRecipeByFollowed(int page, int elements) {
		return recipeRepository
				.findByFollowed(true, authService.getCurrentUser().getUserId(), PageRequest.of(page, elements))
				.map(this::mapEntityDtoList);
	}

	@Transactional(readOnly = true)
	public List<RecipeResponse> getSimilar(String category) {
		List<Recipe> recipe = new ArrayList<>();
		if (category.length() >= 8) {
			for (int i = 0; i < 8; i += 2) {
				String s = category.substring(i, i + 2);
				recipe.addAll(recipeRepository.findRandom(s, true, 3));

			}
		}

		else if (category.length() == 6) {
			for (int i = 0; i < 6; i += 2) {
				String s = category.substring(i, i + 2);
				recipe.addAll(recipeRepository.findRandom(s, true, 4));

			}

		} else if (category.length() == 4) {
			for (int i = 0; i < 4; i += 2) {
				String s = category.substring(i, i + 2);
				recipe.addAll(recipeRepository.findRandom(s, true, 6));

			}

		} else if (category.length() == 2) {

			recipe.addAll(recipeRepository.findRandom(category, true, 12));

		} else if (category == null | category.length() == 0) {
			recipe.addAll(recipeRepository.findRandomWithoutCat(true, 12));
		}

		return recipe.stream().map(this::mapEntityDtoList).collect(toList());
	}

	@Transactional
	@Scheduled(fixedDelay = 86400000)
	public void updateBalance() {
		int page = 0;
		boolean lastPage = false;

		while (!lastPage) {
			Page<Recipe> recipes = recipeRepository
					.findByVoteCountAndApproved(true, PageRequest.of(page, 20, Direction.DESC, "voteCount"))
					.map(this::calculate);
			lastPage = recipes.isLast();
			page++;
		}
	}

	@Transactional(isolation = Isolation.SERIALIZABLE)
	public Recipe calculate(Recipe recipe) {
		//hidden
//		ipRepository.deleteByRecipe(recipe);
		return recipe;

	}

//	private void uploadToStorage(Storage storage, MultipartFile uploadFrom, BlobInfo blobInfo) throws IOException {
//		// For small files:
//
//		// For big files:
//		// When content is not available or large (1MB or more) it is recommended to
//		// write it in chunks via the blob's channel writer.
//		try (WriteChannel writer = storage.writer(blobInfo)) {
//
//			byte[] buffer = new byte[10_240];
//			try (InputStream input = uploadFrom.getInputStream()) {
//				int limit;
//				while ((limit = input.read(buffer)) >= 0) {
//					writer.write(ByteBuffer.wrap(buffer, 0, limit));
//				}
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//
//		}
//		System.out.println("done");
//	}
//
//	public void saveVideo(MultipartFile video1, Long id) {
//		BlobId blobId = BlobId.of("foodys", id + ".mp4");
//		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
//		try {
//			uploadToStorage(storage, video1, blobInfo);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

//		File target = new File("D:\\target.mp4");
//		File source = new File("D:\\source.avi");
//		FileOutputStream fileOutputStream;
//		try {
//			fileOutputStream = new FileOutputStream(source);
//			byte[] bytes = video1.getInputStream().readAllBytes();
//			fileOutputStream.write(bytes);
//			fileOutputStream.flush();
//			fileOutputStream.close();
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}

		/* Step 2. Set Audio Attrributes for conversion */
//		AudioAttributes audio = new AudioAttributes();
//		audio.setCodec("aac");
//		// here 64kbit/s is 64000
//		audio.setBitRate(64000);
//		audio.setChannels(2);
//		audio.setSamplingRate(44100);
//
//		/* Step 3. Set Video Attributes for conversion*/
//		VideoAttributes video = new VideoAttributes();
//		video.setCodec("h264");
//		video.setX264Profile(X264_PROFILE.BASELINE);
//		// Here 160 kbps video is 160000
//		video.setBitRate(1600000);
//		// More the frames more quality and size, but keep it low based on devices like mobile
//		video.setFrameRate(30);
//		video.setSize(new VideoSize(1920, 1080));
//
//		/* Step 4. Set Encoding Attributes*/
//		EncodingAttributes attrs = new EncodingAttributes();
//		attrs.setFormat("mp4");
//		attrs.setAudioAttributes(audio);
//		attrs.setVideoAttributes(video);
//		MultimediaObject m = new MultimediaObject(source);
//
//		/* Step 5. Do the Encoding*/
//		try {
//		  Encoder encoder = new Encoder();
//		  encoder.encode(m, target, attrs);
//		} catch (Exception e) {
//		  /*Handle here the video failure*/ 
//		  e.printStackTrace();
//		}

//		 String projectId = "foodyfolks";
//		    String location = "us-central1";
//		    String inputUri = "gs://foodym/TASK-6.mp4";
//		    String outputUri = "gs://foodym/a/";
//		    String preset = "preset/web-hd";
//		    try (TranscoderServiceClient transcoderServiceClient = TranscoderServiceClient.create()) {
//
//		    	CreateJobRequest createJobRequest =
//		            CreateJobRequest.newBuilder()
//		                .setJob(
//		                    Job.newBuilder()
//		                        .setInputUri(inputUri)
//		                        .setOutputUri(outputUri)
//		                        .setTemplateId(preset)
//		                        .build())
//		                .setParent(LocationName.of(projectId, location).toString())
//		                .build();
//
//		        // Send the job creation request and process the response.
//		        Job job = transcoderServiceClient.createJob(createJobRequest);
//		        System.out.println("Job: " + job.getName());
//		      } catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}

//		try {
//			@SuppressWarnings("deprecation")
//			BlobInfo blobInfo = storage.create(BlobInfo.newBuilder("foodym", id + "_360.mp4")
////                                 .setAcl(new ArrayList<>(Arrays.asList(Acl.of(User.ofAllUsers(), Role.READER))))
//					.build(), video1.getInputStream()
//
//			);

//			BlobInfo blobInfo2 = storage.create(BlobInfo.newBuilder("foodym", id + "_1080.mp4")
////                  .setAcl(new ArrayList<>(Arrays.asList(Acl.of(User.ofAllUsers(), Role.READER))))
//					.build(), 
//					compressor.encodeVideoWithAttributes(video.getBytes(), VideoFormats.MP4,audioAttribute, videoAttribute)
//
//			);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}


