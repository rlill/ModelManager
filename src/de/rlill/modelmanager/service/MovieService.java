package de.rlill.modelmanager.service;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.util.SparseArray;
import de.rlill.modelmanager.R;
import de.rlill.modelmanager.Util;
import de.rlill.modelmanager.model.Diary;
import de.rlill.modelmanager.model.Event;
import de.rlill.modelmanager.model.Model;
import de.rlill.modelmanager.model.MovieModel;
import de.rlill.modelmanager.model.Movieproduction;
import de.rlill.modelmanager.persistance.DiaryDbAdapter;
import de.rlill.modelmanager.persistance.EventDbAdapter;
import de.rlill.modelmanager.persistance.MovieModelDbAdapter;
import de.rlill.modelmanager.persistance.MovieproductionDbAdapter;
import de.rlill.modelmanager.struct.EventClass;
import de.rlill.modelmanager.struct.EventFlag;
import de.rlill.modelmanager.struct.ModelStatus;
import de.rlill.modelmanager.struct.MovieStatus;
import de.rlill.modelmanager.struct.MovieType;


public class MovieService {

	private static final String LOG_TAG = "MM*" + MovieService.class.getSimpleName();

	private static SparseArray<PriceRange> moviePriceRange = null;
	private static SparseArray<Movieproduction> movieProductions = null;

	public static int movieStartCostAvg(MovieType type) {
		if (moviePriceRange == null) initMoviePriceRanges();
		PriceRange pr = moviePriceRange.get(type.getIndex());
		return Util.interpolation(pr.startMin,  pr.startMax);
	}

	public static int movieProgressCostAvg(MovieType type) {
		if (moviePriceRange == null) initMoviePriceRanges();
		PriceRange pr = moviePriceRange.get(type.getIndex());
		return Util.interpolation(pr.progressMin,  pr.progressMax);
	}

	public static int movieFinishCostAvg(MovieType type) {
		if (moviePriceRange == null) initMoviePriceRanges();
		PriceRange pr = moviePriceRange.get(type.getIndex());
		return Util.interpolation(pr.finishMin,  pr.finishMax);
	}

	public static List<Movieproduction> listAllMovies() {
		List<Movieproduction> result = new ArrayList<Movieproduction>();
		if (movieProductions == null) getMovie(1);
		for (int i = 0; i < movieProductions.size(); i++) {
			result.add(movieProductions.valueAt(i));
		}
		return result;
	}

	public static void startMovie(String name, MovieType type, int startDay) {
		Movieproduction mp = new Movieproduction();
		mp.setName(name);
		mp.setType(type);
		mp.setStatus(MovieStatus.PLANNED);
		mp.setStartDay(startDay);
		MovieproductionDbAdapter.addMovieproduction(mp);
		movieProductions = null; // must re-read cache
	}

	public static void sellMovie(int movieId) {
		Movieproduction mpr = getMovie(movieId);
		EventFlag flag = flagForMovieType(mpr.getType());
		String msg = MessageService.getMessage(R.string.display_msg_movie_finished).replace("%M", mpr.getName());
		DiaryService.log(msg.replace("%M", mpr.getName()), EventClass.MOVIE_FINISH, flag, movieId, 0);

		// finishing cost
		Event e = EventDbAdapter.getAllEvents(EventClass.MOVIE_FINISH, flag).get(0);
		int fcost = Util.niceRandom(e.getAmountMin(), e.getAmountMax());
		msg = e.getDescription().replace("%M", mpr.getName());
		DiaryService.log(msg, EventClass.MOVIE_FINISH, flag, movieId, fcost);

		int price = getMovieValue(movieId);
		msg = MessageService.getMessage(R.string.display_msg_movie_sold).replace("%M", mpr.getName());
		TransactionService.transfer(-1, 0, price, msg);

		setMovieStatus(movieId, MovieStatus.SOLD, price);
	}

	public static void rentMovie(int movieId) {
		Movieproduction mpr = getMovie(movieId);
		EventFlag flag = flagForMovieType(mpr.getType());
		String msg = MessageService.getMessage(R.string.display_msg_movie_finished);
		DiaryService.log(msg.replace("%M", mpr.getName()), EventClass.MOVIE_FINISH, flag, movieId, 0);

		// finishing cost
		Event e = EventDbAdapter.getAllEvents(EventClass.MOVIE_FINISH, flag).get(0);
		int fcost = Util.niceRandom(e.getAmountMin(), e.getAmountMax());
		msg = e.getDescription().replace("%M", mpr.getName());
		DiaryService.log(msg, EventClass.MOVIE_FINISH, flag, movieId, fcost);

		int price = getMovieValue(movieId);
		msg = MessageService.getMessage(R.string.display_msg_movie_rented).replace("%M", mpr.getName());
		TransactionService.transfer(-1, 0, price / 10, msg);	// 10% as first rate

		setMovieStatus(movieId, MovieStatus.RENTAL, price);
	}

	public static void abortMovie(int movieId) {
		Movieproduction mpr = getMovie(movieId);
		EventFlag flag = flagForMovieType(mpr.getType());
		String msg = MessageService.getMessage(R.string.display_msg_movie_abort);
		DiaryService.log(msg.replace("%M", mpr.getName()), EventClass.MOVIE_FINISH, flag, movieId, 0);

		setMovieStatus(movieId, MovieStatus.CANCELED, 0);
	}

	public static Movieproduction getMovie(int movieId) {
		if (movieProductions == null) {
			movieProductions = new SparseArray<Movieproduction>();
			for (Movieproduction mpr : MovieproductionDbAdapter.listMovieproductions()) {
				movieProductions.put(mpr.getId(), mpr);
			}
		}
		return movieProductions.get(movieId);
	}

	public static int getMovieCost(int movieId) {
		Movieproduction mpr = getMovie(movieId);
		int expenses = 0;
		for (Diary diary : DiaryDbAdapter.listEvents(movieId, mpr.getStartDay())) {
			if ((diary.getEventClass() == EventClass.MOVIE_START || diary.getEventClass() == EventClass.MOVIE_PROGRESS)
				&& diary.getModelId() == movieId) {
				expenses += diary.getAmount();
			}
		}
		for (MovieModel mm : MovieModelDbAdapter.getMovieModels(movieId, 0, 0)) {
			expenses += mm.getPrice();
		}

		return expenses;
	}

	public static int getMovieValue(int movieId) {
		Movieproduction mpr = getMovie(movieId);

		// defaults for ENTERTAINMENT:
		int limit1 = 13;
		int limit2 = 24;
		int limit3 = 29;
		switch (mpr.getType()) {
		case EROTIC:
			limit1 = 10;
			limit2 = 16;
			limit3 = 24;
			break;
		case PORN:
			limit1 = 7;
			limit2 = 12;
			limit3 = 18;
			break;
		default:
		}

		int duration = DiaryService.today() - mpr.getStartDay();
		if (duration < limit1) return 0;

		int expenses = getMovieCost(movieId);
		if (duration < limit2) return (int)(expenses * 0.75);
		if (duration < limit3) return (int)(expenses * 1.1);
		return (int)(expenses * 1.5);
	}

	public static void addModelForMovie(int movieId, int modelId, int day, int price) {

		// avoid duplicates:
		List<MovieModel> mmList = MovieModelDbAdapter.getMovieModels(movieId, modelId, day);
		if (mmList.size() > 0) {
			// update instead
			MovieModel mm = mmList.get(0);
			mm.setPrice(price);
			MovieModelDbAdapter.updateMovieModel(mm);
			return;
		}

		MovieModel mm = new MovieModel();
		mm.setMovieId(movieId);
		mm.setModelId(modelId);
		mm.setDay(day);
		mm.setPrice(price);
		MovieModelDbAdapter.addMovieModel(mm);
	}

	public static void removeModelFromMovie(int movieId, int modelId) {
		List<MovieModel> mmList = MovieModelDbAdapter.getMovieModels(movieId, modelId, DiaryService.today() + 1);
		if (mmList.size() > 0) {
			MovieModel mm = mmList.get(0);
			MovieModelDbAdapter.deleteMovieModel(mm.getId());
			return;
		}
		Model model = ModelService.getModelById(modelId);
		if (model.getStatus() == ModelStatus.MOVIEPROD)
			ModelService.setModelStatus(modelId, ModelStatus.HIRED);
	}

	/** @param day (optional) 0 = all */
	public static List<MovieModel> getModelsForMovie(int movieId, int day) {
		return MovieModelDbAdapter.getMovieModels(movieId, 0, day);
	}

	public static List<MovieModel> getMoviesForModel(int modelId) {
		return MovieModelDbAdapter.getMovieModels(0, modelId, 0);
	}

	public static List<MovieModel> getModelsInMovieTomorrow() {
		return MovieModelDbAdapter.getMovieModels(0, 0, DiaryService.today() + 1);
	}

	public static List<Movieproduction> getCurrentMovies() {
		List<Movieproduction> result = new ArrayList<Movieproduction>();
		if (movieProductions == null) getMovie(1);
		for (int i = 0; i < movieProductions.size(); i++) {
			Movieproduction mpr = movieProductions.valueAt(i);
			if ((mpr.getStatus() == MovieStatus.PLANNED || mpr.getStatus() == MovieStatus.IN_PROGRESS)
					&& mpr.getStartDay() <= DiaryService.today()) {
				result.add(mpr);
			}
		}
		return result;
	}

	public static List<Movieproduction> getRentedMovies() {
		List<Movieproduction> result = new ArrayList<Movieproduction>();
		if (movieProductions == null) getMovie(1);
		for (int i = 0; i < movieProductions.size(); i++) {
			Movieproduction mpr = movieProductions.valueAt(i);
			if (mpr.getStatus() == MovieStatus.RENTAL) result.add(mpr);
		}
		return result;
	}

	public static void setMovieStatus(int movieId, MovieStatus status) {
		Movieproduction mpr = getMovie(movieId);
		mpr.setStatus(status);
		MovieproductionDbAdapter.updateMovieproduction(mpr);
	}

	public static void setMovieStatus(int movieId, MovieStatus status, int price) {
		Movieproduction mpr = getMovie(movieId);
		mpr.setStatus(status);
		mpr.setPrice(price);
		MovieproductionDbAdapter.updateMovieproduction(mpr);
	}

	public static EventFlag flagForMovieType(MovieType type) {
		switch (type) {
		case ENTERTAINMENT:
			return EventFlag.MOVIE_ENTERTAIN;
		case EROTIC:
			return EventFlag.MOVIE_EROTIC;
		case PORN:
			return EventFlag.MOVIE_PORN;
		}
		throw new IllegalArgumentException("Illegal type " + type);
	}

	private static void initMoviePriceRanges() {
		moviePriceRange = new SparseArray<PriceRange>();
		for (Event event : EventDbAdapter.getAllEvents(EventClass.MOVIE_START, null)) {
			PriceRange pr = new PriceRange();
			pr.startMin = event.getAmountMin();
			pr.startMax = event.getAmountMax();
			switch (event.getFlag()) {
			case MOVIE_ENTERTAIN:
				moviePriceRange.put(MovieType.ENTERTAINMENT.getIndex(), pr);
				break;
			case MOVIE_EROTIC:
				moviePriceRange.put(MovieType.EROTIC.getIndex(), pr);
				break;
			case MOVIE_PORN:
				moviePriceRange.put(MovieType.PORN.getIndex(), pr);
				break;
			default:
			}
		}
		for (Event event : EventDbAdapter.getAllEvents(EventClass.MOVIE_PROGRESS, null)) {
			PriceRange pr = null;
			switch (event.getFlag()) {
			case MOVIE_ENTERTAIN:
				pr = moviePriceRange.get(MovieType.ENTERTAINMENT.getIndex());
				break;
			case MOVIE_EROTIC:
				pr = moviePriceRange.get(MovieType.EROTIC.getIndex());
				break;
			case MOVIE_PORN:
				pr = moviePriceRange.get(MovieType.PORN.getIndex());
				break;
			default:
			}
			if (pr == null) {
				Log.e(LOG_TAG, "Progress PriceRange for " + event.getFlag() + " not found");
				continue;
			}
			pr.progressMin = event.getAmountMin();
			pr.progressMax = event.getAmountMax();
		}
		for (Event event : EventDbAdapter.getAllEvents(EventClass.MOVIE_FINISH, null)) {
			PriceRange pr = null;
			switch (event.getFlag()) {
			case MOVIE_ENTERTAIN:
				pr = moviePriceRange.get(MovieType.ENTERTAINMENT.getIndex());
				break;
			case MOVIE_EROTIC:
				pr = moviePriceRange.get(MovieType.EROTIC.getIndex());
				break;
			case MOVIE_PORN:
				pr = moviePriceRange.get(MovieType.PORN.getIndex());
				break;
			default:
			}
			if (pr == null) {
				Log.e(LOG_TAG, "Finish PriceRange for " + event.getFlag() + " not found");
				continue;
			}
			pr.finishMin = event.getAmountMin();
			pr.finishMax = event.getAmountMax();
		}
	}

	public static class PriceRange {
		public int startMin;
		public int startMax;
		public int progressMin;
		public int progressMax;
		public int finishMin;
		public int finishMax;
	}
}
