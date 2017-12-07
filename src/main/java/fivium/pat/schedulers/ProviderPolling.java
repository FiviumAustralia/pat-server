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
		logger.info("Current Time: "+currentTime.getTime());
		logger.info("Instance at 5:59 : "+calendarAt559.getTime());
		logger.info("Instance at 6:15 : "+calendarAt615.getTime());
		if (!(currentTime.getTime().after(calendarAt559.getTime()) && currentTime.getTime().before(calendarAt615.getTime()))) {
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
		calendar.set(Calendar.HOUR_OF_DAY, 17);
		return calendar;
	}
	
	private Calendar setCalendarAt615pm() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 15);
		calendar.set(Calendar.HOUR_OF_DAY, 18);
		return calendar;
	}
}
