package fivium.pat.provider.data;

import java.util.Date;

public class UserData {

	private String firstName;
	private String lastName;
	private String activityProvider;
	private Date providerAuthorizationDate;
	private String companyName;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getActivityProvider() {
		return activityProvider;
	}

	public void setActivityProvider(String activityProvider) {
		this.activityProvider = activityProvider;
	}

	public Date getProviderAuthorizationDate() {
		return providerAuthorizationDate;
	}

	public void setProviderAuthorizationDate(Date providerAuthorizationDate) {
		this.providerAuthorizationDate = providerAuthorizationDate;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

}
