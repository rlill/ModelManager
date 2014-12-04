package de.rlill.modelmanager.service;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.SparseArray;
import de.rlill.modelmanager.R;
import de.rlill.modelmanager.Util;
import de.rlill.modelmanager.model.CompanyCar;
import de.rlill.modelmanager.model.Diary;
import de.rlill.modelmanager.model.Model;
import de.rlill.modelmanager.model.ModelTraining;
import de.rlill.modelmanager.model.Training;
import de.rlill.modelmanager.persistance.ModelTrainingDbAdapter;
import de.rlill.modelmanager.service.ModelService.Statistics;
import de.rlill.modelmanager.struct.CarClass;
import de.rlill.modelmanager.struct.EventClass;
import de.rlill.modelmanager.struct.EventFlag;
import de.rlill.modelmanager.struct.RejectReasons;

public class MessageService {

	private static SparseArray<String> messages = new SparseArray<String>();

	public static void translate(Context ctx) {
		Resources res = ctx.getResources();

		translate(res, R.string.accountmessage_bonus);
		translate(res, R.string.accountmessage_company_car_costs);
		translate(res, R.string.accountmessage_reclaim);
		translate(res, R.string.accountmessage_credit_pay);
		translate(res, R.string.accountmessage_credit_payback);

		translate(res, R.string.logmessage_bonus);
		translate(res, R.string.logmessage_sick_today);
		translate(res, R.string.logmessage_training_today);
		translate(res, R.string.logmessage_vacation_today);
		translate(res, R.string.logmessage_become_unavailable);
		translate(res, R.string.logmessage_become_available);
		translate(res, R.string.logmessage_dismissal_imm);
		translate(res, R.string.logmessage_change_team);
		translate(res, R.string.logmessage_model_quit);
		translate(res, R.string.logmessage_car_repair2);
		translate(res, R.string.logmessage_car_repair3);
		translate(res, R.string.logmessage_car_breakdown);
		translate(res, R.string.logmessage_car_accident);
		translate(res, R.string.logmessage_car_wrecked);
		translate(res, R.string.logmessage_car_stolen);
		translate(res, R.string.logmessage_car_takeaway);
		translate(res, R.string.logmessage_car_buy);

		translate(res, R.string.display_msg_movie_abort);
		translate(res, R.string.display_msg_movie_sold);
		translate(res, R.string.display_msg_movie_rented);
	}

	private static void translate(Resources res, int id) {
		messages.put(id, res.getString(id));
	}

	public static String getMessage(int id) {
		String msg = messages.get(id, null);
		if (msg == null) return String.format("Error (%08x)", id);
		return msg;
	}

	public static String getMessage(int id, Model model) {
		String msg = messages.get(id, null);
		if (msg == null) return String.format("Error (%08x)", id);
		msg = msg.replace("%N", model.getFullname());
		return msg;
	}

	public static String getMessage(int id, CompanyCar cc) {
		String msg = messages.get(id, null);
		if (msg == null) return String.format("Error (%08x)", id);
		msg = msg.replace("%C", cc.getLicensePlate());
		return msg;
	}

	public static String getMessage(int id, Model model, Training training) {
		String msg = messages.get(id, null);
		if (msg == null) return String.format("Error (%08x)", id);
		msg = msg.replace("%N", model.getFullname());
		msg = msg.replace("%U", training.getDescription());
		return msg;
	}

	public static String fillOutLogmessage(String desc, Model m, int amount1, int amount2) {
		desc = desc.replace("%A", Util.amount(amount1));
		desc = desc.replace("%B", Util.amount(amount2));
		if (m != null) {
			desc = desc.replace("%N", m.getFullname());
			desc = desc.replace("%S", Util.amount(m.getSalary()));
			desc = desc.replace("%V", Integer.toString(m.getVacrest()));
			if (desc.contains("%C")) desc = desc.replace("%C", CarService.getCarInfo(m.getCarId()));
			if (desc.contains("%T")) desc = desc.replace("%T", ModelService.getTeamName(m.getTeamId()));
		}
		if (amount2 > 0 && desc.contains("%T")) {
			desc = desc.replace("%T", ModelService.getTeamName(amount2));
		}
		return desc;
	}

	public static String applicationSelfPresentation(Context ctx,
			Model model, int expectSalary, int expectVacation, int expectBonus, CarClass expectCarClass) {
		// properties: quality_photo; quality_movie; quality_tlead; erotic; ambition; health;

		StringBuilder result = new StringBuilder();

		// introduction
		result.append(ctx.getResources().getString(R.string.display_msg_appl_intro)).append(' ');

		List<String> goodQualities = new ArrayList<String>();
		List<String> bestQualities = new ArrayList<String>();

		if (model.getQuality_photo() > 66) bestQualities.add(ctx.getResources().getString(R.string.display_msg_appl_model_photo));
		else if (model.getQuality_photo() > 33) goodQualities.add(ctx.getResources().getString(R.string.display_msg_appl_model_photo));

		if (model.getQuality_movie() > 66) bestQualities.add(ctx.getResources().getString(R.string.display_msg_appl_model_movie));
		else if (model.getQuality_movie() > 33) goodQualities.add(ctx.getResources().getString(R.string.display_msg_appl_model_movie));

		if (model.getQuality_tlead() > 66) bestQualities.add(ctx.getResources().getString(R.string.display_msg_appl_teamleader));
		else if (model.getQuality_tlead() > 33) goodQualities.add(ctx.getResources().getString(R.string.display_msg_appl_teamleader));

		if (goodQualities.size() > 0) {
			result.append(ctx.getResources().getString(R.string.display_msg_appl_qualities))
				.append(' ')
				.append(TextUtils.join(" " + ctx.getResources().getString(R.string.display_msg_appl_conjunction) + " ", goodQualities))
				.append(' ');
			if (bestQualities.size() > 0) {
				result.append(ctx.getResources().getString(R.string.display_msg_appl_and_top_qualities))
					.append(' ')
					.append(TextUtils.join(" " + ctx.getResources().getString(R.string.display_msg_appl_conjunction) + " ", bestQualities))
					.append(' ');
			}
		}
		else if (bestQualities.size() > 0) {
			result.append(ctx.getResources().getString(R.string.display_msg_appl_only_top_qualities))
				.append(' ')
				.append(TextUtils.join(" " + ctx.getResources().getString(R.string.display_msg_appl_conjunction) + " ", bestQualities))
				.append(' ');
		}
		result.deleteCharAt(result.length() - 1).append('.');

		if (model.getErotic() > 33)
			result.append(' ').append(ctx.getResources().getString(R.string.display_msg_appl_erotic));

		if (model.getAmbition() > 50)
			result.append(' ').append(ctx.getResources().getString(R.string.display_msg_appl_ambition));

		if (model.getHealth() > 66)
			result.append(' ').append(ctx.getResources().getString(R.string.display_msg_appl_health));

		// earlier work experiences
		if (model.getSalary() > 0) {
			String exp = ctx.getResources().getString(R.string.display_msg_appl_experiences);
			exp = exp.replace("%S", Util.amount(model.getSalary()));
			result.append('\n').append(exp);
			int from = 0;
			int to = 0;
			for (Diary diary : DiaryService.listEventsForModel(model.getId())) {
				if (diary.getEventClass() == EventClass.ACCEPT && diary.getEventFlag() == EventFlag.QUIT)
					to = diary.getDay();
				else if (diary.getEventClass() == EventClass.ACCEPT && diary.getEventFlag() == EventFlag.HIRE) {
					from = diary.getDay();
					String per = ctx.getResources().getString(R.string.display_msg_appl_experience);
					per = per.replace("%F", Integer.toString(from));
					per = per.replace("%T", Integer.toString(to));
					result.append('\n').append(per);
					to = 0;
				}
			}
		}

		if (expectSalary > 0) {
			result.append('\n').append(ctx.getResources().getString(R.string.display_msg_appl_expect_salary).replace("%S", Util.amount(expectSalary)));
		}

		return result.toString();
	}

	public static String recentWorkPresentation(Context ctx, Model model) {

		// introduction
		String result = ctx.getResources().getString(R.string.display_msg_work_presentation);

		Statistics st = ModelService.getStatistics(model.getId());
		String sessPh = ctx.getResources().getString((st.w4photoSessions == 1) ? R.string.labelPhotoSession_si : R.string.labelPhotoSession_pl);
		String sessMv = ctx.getResources().getString((st.w4movieSessions == 1) ? R.string.labelMovieSession_si : R.string.labelMovieSession_pl);

		result = result.replace("%W", " " + st.w4photoSessions + " " + sessPh + " "
				+ ctx.getResources().getString(R.string.display_msg_work_conjunction)
				+ " " + st.w4movieSessions + " " + sessMv);
		result = result.replace("%E", Util.amount(st.w4photoEarnings + st.w4movieEarnings));
		result = result.replace("%B", Util.amount(st.w4bonus));

		return result;
	}

	private static String qualificationStmt(Context ctx, String qualification, int oldVal, int improve) {
		int newVal = oldVal + improve;
		if (newVal < 0) newVal = 0;
		if (newVal > 100) newVal = 100;
		int realImprove = newVal - oldVal;
		if (realImprove > 30) {
			String improveStr = ctx.getResources().getString(R.string.display_msg_training_result_improve_more);
			return improveStr.replace("%Q", qualification);
		}
		else if (realImprove > 0) {
			String improveStr = ctx.getResources().getString(R.string.display_msg_training_result_improve);
			return improveStr.replace("%Q", qualification);
		}
		else if (realImprove < 0) {
			String improveStr = ctx.getResources().getString(R.string.display_msg_training_result_degrade);
			return improveStr.replace("%Q", qualification);
		}
		return null;
	}

	public static String trainingFinishedReport(Context ctx, Model model, Training training) {
		StringBuilder result = new StringBuilder();
		String msg = ctx.getResources().getString(R.string.display_msg_training_finished);
		result.append(msg.replace("%N", model.getFullname()).replace("%U", training.getDescription()));

		String qualification = ctx.getResources().getString(R.string.display_msg_training_qualification_as);
		msg = qualificationStmt(ctx, qualification + " " + ctx.getResources().getString(R.string.labelQualityPhoto),
				model.getQuality_photo(), training.getInc_qphoto());
		if (msg != null) result.append('\n').append(msg);

		msg = qualificationStmt(ctx, qualification + " " + ctx.getResources().getString(R.string.labelQualityMovie),
				model.getQuality_movie(), training.getInc_qmovie());
		if (msg != null) result.append('\n').append(msg);

		msg = qualificationStmt(ctx, qualification + " " + ctx.getResources().getString(R.string.labelQualityTLead),
				model.getQuality_tlead(), training.getInc_qtlead());
		if (msg != null) result.append('\n').append(msg);

		msg = qualificationStmt(ctx, ctx.getResources().getString(R.string.labelAmbition),
				model.getAmbition(), training.getInc_ambition());
		if (msg != null) result.append('\n').append(msg);

		msg = qualificationStmt(ctx, ctx.getResources().getString(R.string.labelCriminal),
				model.getCriminal(), training.getInc_criminal());
		if (msg != null) result.append('\n').append(msg);

		msg = qualificationStmt(ctx, ctx.getResources().getString(R.string.labelErotic),
				model.getErotic(), training.getInc_erotic());
		if (msg != null) result.append('\n').append(msg);

		msg = qualificationStmt(ctx, ctx.getResources().getString(R.string.labelHealth),
				model.getHealth(), training.getInc_health());
		if (msg != null) result.append('\n').append(msg);

		msg = qualificationStmt(ctx, ctx.getResources().getString(R.string.labelMood),
				model.getMood(), training.getInc_mood());
		if (msg != null) result.append('\n').append(msg);

		return result.toString();
	}

	public static String workRejectReason(Context ctx, Model model) {
		StringBuilder result = new StringBuilder();
		RejectReasons rr = ModelService.bookingRejectReasons(model.getId());
		if (rr.carMissing) result.append(ctx.getResources().getString(R.string.display_msg_bookreject_no_car)).append('\n');
		if (rr.bonusMissing > 0) result.append(ctx.getResources().getString(
				R.string.display_msg_bookreject_bonus_missing, Util.amount(rr.bonusMissing))).append('\n');
		if (rr.vacationMissing > 0) result.append(ctx.getResources().getString(
				R.string.display_msg_bookreject_vacation)).append('\n');
		if (rr.badMood) result.append(ctx.getResources().getString(R.string.display_msg_bookreject_bad_mood)).append('\n');
		return result.toString();
	}

	public static String trainingHistory(Context ctx, int modelId) {
		StringBuilder result = new StringBuilder();
		String day_s = ctx.getResources().getString(R.string.display_day_si);
		for (ModelTraining mt : ModelTrainingDbAdapter.getTrainingsForModel(modelId)) {
			if (mt.getTraining() == null) continue;
			if (result.length() == 0) {
				int past = DiaryService.today() - mt.getStartDay();
				result.append(ctx.getResources().getString(R.string.display_msg_training_last_training, Util.duration(ctx, past)));
			}
			result.append('\n').append('(').append(mt.getTrainingStatus().getName()).append(") ")
				.append(day_s).append(' ').append(mt.getStartDay()).append('-').append(mt.getEndDay()).append(": ")
				.append(mt.getTraining().getDescription()).append(' ').append(Util.amount(mt.getPrice()));
		}
		if (result.length() == 0) {
			result.append(ctx.getResources().getString(R.string.display_msg_training_no_training));
		}
		return result.toString();
	}
}
