package fivium.pat.schedulers;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import fivium.pat.provider.utils.FitbitDataRetriever;

public class ProviderPolling implements Job {

	private static Log logger = LogFactory.getLog(ProviderPolling.class);

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		Calendar calendarAt559 = setCalendarAt6pm();
		Calendar calendarAt615 = setCalendarAt615pm();
		Calendar currentTime = Calendar.getInstance();
		if (!(currentTime.after(calendarAt559) && currentTime.before(calendarAt615))) {
			logger.info("Job: Polling Fitbit for data...");
			FitbitDataRetriever.pollFitbitForData(false);
		} else {
			logger.info("Job: Polling Fitbit for data and generate notifications...");
			FitbitDataRetriever.pollFitbitForData(true);
		}

	}

	private Calendar setCalendarAt6pm() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.HOUR, 17);
		return calendar;
	}
	
	private Calendar setCalendarAt615pm() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 15);
		calendar.set(Calendar.HOUR, 18);
		return calendar;
	}
}
