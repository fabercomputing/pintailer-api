package com.fw.utils;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.activation.DataSource;
import javax.activation.FileDataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.fw.bean.AutomationReportBean;
import com.fw.bean.ProgressReportBean;
import com.fw.config.CustomUser;
import com.fw.controller.IUsersController;
import com.fw.domain.ClientProjects;
import com.fw.exceptions.APIExceptions;
import com.fw.pintailer.constants.PintailerConstants;
import com.fw.services.IClientProjectsService;
import com.fw.services.IEmailService;
//import com.fw.config.Constants;
//import com.fw.domain.SentMessages;
//import com.fw.enums.ContactStatus;
//import com.fw.enums.ContactType;
import com.fw.services.IReportsService;

@Service
public class EmailUtils {

	private Logger log = Logger.getLogger(EmailUtils.class);

	@Value("${spring.mail.username}")
	private String senderEmailAddress;
	@Value("${spring.mail.displayName}")
	private String senderDisplayName;

	@Autowired
	private Environment env;

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	IReportsService reportsService;

	@Autowired
	IUsersController usersController;

	@Autowired
	IClientProjectsService clientProjectsService;

	@Autowired
	IEmailService emailService;

	private static final String MAIL_TO = "pintailerapp@gmail.com";
	private static final String RECIEPIENT_USERNAME = "Pintailer Team Member";
	private String SUBJECT = "Pintailer support request - #visitorName#";

	private String REPORTS_SUBJECT = "Pintailer - Progress Report for #orgName#";

	private String TEMPLATE_MESSAGE = "Hello #UserName#,<BR>We have a new "
			+ "#supportType# from following visitor : <BR>Name : #visitorName# "
			+ "<BR> Email : #visitorEmail# <BR> Company Name : #visitorCompanyName# "
			+ "<BR> Work Phone No. : #visitorContactNo# <BR> Remarks : #remarks# "
			+ "<BR><BR>Thanks and Regards,<BR>Pintailer Support Team";

	private String TEMPLATE_TABLE = ""
			+ "<p style=\"font-family: Arial, Helvetica, sans-serif;\"> Added: <span style=\"color:green\">#TotalTcAdded# &#x25B2;</span>: #TotalTcAddedUsers# "
			+ "<br /><hr /> Deleted: <span style=\"color:red\">#TotalTcDeleted# &#x25BC;</span>: #TotalTcDeletedUsers# "
			+ " <br /><hr /> Automation Done: <span style=\"color:green\"> #TotalMappingAdded#  &#x25B2;</span>: #TotalMappingAddedUsers# "
			+ " <br /><hr /> Automation Removed: <span style=\"color:red\"> #TotalMappingDeleted# &#x25BC;</span>: #TotalMappingDeletedUsers# </p>"
			+ "<table style=\"font-family: arial; sans-serif; border-collapse: collapse; width: 100%;\">"
			+ "<tr> <th></th> <th>Test Case Type</th> <th>Total</th> <th>% out of Total</th> </tr> "
			+ "<tr style=\"background-color: #f2f2f2;\"> <td style=\"background-color: rgb(152, 171, 197);\"> <td>Total</td> <td style=\"color:#TotalTestCaseColor#\">#TotalTestCase#</td> <td>#TotalTestCasePercent#</td> </tr> "
			+ "<tr> <td style=\"background-color: rgb(138, 137, 166);\"> <td>Automatable</td> <td>#AutomatableTestCase#</td> <td>#AutomatableTestCasePercent#</td> </tr>"
			+ " <tr style=\"background-color: #f2f2f2;\"> <td style=\"background-color: rgb(123, 104, 136);\"> <td>  Automation Done</td> <td>#AutoDoneTestCase#</td> <td>#AutoDoneTestCasePercent#</td> </tr>"
			+ " <tr> <td style=\"background-color:rgb(107, 72, 107);\"> <td>  Automation Pending	</td> <td>#AutoPendingTestCase#</td> <td>#AutoPendingTestCasePercent#</td> </tr> "
			+ "<tr style=\"background-color: #f2f2f2;\"> <td style=\"background-color: rgb(160, 93, 86);\"> <td>Non-Automatable	</td> <td>#NonAutoTestCase#</td> <td>#NonAutoTestCasePercent#</td> </tr> "
			+ "</table>" + "<hr /><br />";

	private String TEMPLATE_GRAPH = "<div>"
			+ "<div style=\"width: #TotalTestCasePercent#; font: 10px sans-serif; background-color: rgb(152, 171, 197); text-align: right; padding: 3px; margin: 1px; color: white;\">#TotalTestCase#</div>"
			+ " <div style=\"width: #AutomatableTestCasePercent#; font: 10px sans-serif; background-color: rgb(138, 137, 166); text-align: right; padding: 3px; margin: 1px; color: white;\">#AutomatableTestCase#</div>"
			+ " <div style=\"width: #AutoDoneTestCasePercent#; font: 10px sans-serif; background-color: rgb(123, 104, 136); text-align: right; padding: 3px; margin: 1px; color: white;\">#AutoDoneTestCase#</div>"
			+ " <div style=\"width: #AutoPendingTestCasePercent#; font: 10px sans-serif; background-color: rgb(107, 72, 107); text-align: right; padding: 3px; margin: 1px; color: white;\">#AutoPendingTestCase#</div>"
			+ " <div style=\"width: #NonAutoTestCasePercent#; font: 10px sans-serif; background-color: rgb(160, 93, 86); text-align: right; padding: 3px; margin: 1px; color: white;\">#NonAutoTestCase#</div>"
			+ " </div>";

	public String emailSender(String supportType, String visitorName, String visitorEmail, String visitorCompanyName,
			String visitorContactNo, String remarks) {
		String message = TEMPLATE_MESSAGE.replace("#UserName#", RECIEPIENT_USERNAME)
				.replace("#supportType#", supportType).replace("#visitorName#", visitorName)
				.replace("#visitorEmail#", visitorEmail).replace("#visitorCompanyName#", visitorCompanyName)
				.replace("#visitorContactNo#", visitorContactNo).replace("#remarks#", remarks);
		sendMail(MAIL_TO, SUBJECT.replace("#visitorName#", visitorName), message);
		return "Success";
	}

	public void sendMail(String to, String subject, String body) {
		MimeMessagePreparator messagePreparator = mimeMessage -> {
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true,
					PintailerConstants.ENCODING_UTF_8);
			messageHelper.setFrom(senderDisplayName + "<" + senderEmailAddress + ">");
			messageHelper.setTo(to);
			messageHelper.setSubject(subject);
			messageHelper.setText(body, true);
		};
		try {
			log.info("Sending email...");
			javaMailSender.send(messagePreparator);
			log.info("Email sent!");
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
	}

	public void sendReportsMail(String to, String subject, String body, String organization) {
		MimeMessagePreparator messagePreparator = mimeMessage -> {
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true,
					PintailerConstants.ENCODING_UTF_8);
			messageHelper.setFrom(senderDisplayName + "<" + senderEmailAddress + ">");
			messageHelper.setTo(to);
			messageHelper.setSubject(subject);
			messageHelper.setText(body, true);
			DataSource fds = new FileDataSource("pintailer-coverage" + organization + ".png");
			messageHelper.addInline("<pintailerimage>", fds);
			messageHelper.addAttachment("pintailer-coverage" + organization + ".png", fds);
		};
		try {
			log.info("Sending Reports email to " + to);
			javaMailSender.send(messagePreparator);
			log.info("Reports Email sent!");
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
	}

	// once half minute cron = "0 0/1 * * * ?" ,
	// weekday at 4 : cron = "0 0 16 * * MON-FRI"

	@Scheduled(cron = "0 0 16 * * MON-FRI")
	public void sendPeriodicReports() throws APIExceptions, IOException {

		for (int loopi = 0; loopi < 2; loopi++) {

			if (!emailService.isEmailActive(PintailerConstants.EMAIL_TYPE_CLIENT_STATUS)) {
				log.info("Client status email service is not active");
				return;
			}

			String[] mailUserList = {};
			if (loopi == 0) {
//				this loop is for admin users
				mailUserList = env.getProperty("report.user.mail.list").split(";");
			} else {
//				this loop is for testing users
				mailUserList = env.getProperty("report.user.tester.mail.list").split(";");
			}
			String organization = "";

			for (int org = 0; org < mailUserList.length; org++) {

				organization = mailUserList[org].substring(0, mailUserList[org].indexOf("-"));

				String[] userList = mailUserList[org]
						.substring(mailUserList[org].indexOf("-") + 1, mailUserList[org].length()).split(",");

				AutomationReportBean automationReportBean = new AutomationReportBean();

				ProgressReportBean progressReportData = new ProgressReportBean();

				UserDetails userDetails = new CustomUser("pintailer-report-generator", "",
						Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN")), null, null, null, null, organization,
						null, null);

				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userDetails, null, Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN")));

				SecurityContextHolder.getContext().setAuthentication(authentication);

				List<ClientProjects> projects = new ArrayList<ClientProjects>();
				projects = clientProjectsService.getAllClientProjectsForGivenOrg(organization);

				String MailData = "";

				for (int i = 0; i < projects.size(); i++) {

//				Get last week time
					final Calendar cal = Calendar.getInstance();
					cal.add(Calendar.DATE, -1);
					String previousDate = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());

					final Calendar calNew = Calendar.getInstance();
					calNew.add(Calendar.DATE, 1);
					String thisWeekDate = new SimpleDateFormat("yyyy-MM-dd").format(calNew.getTime());

					automationReportBean = reportsService.getAutomationReport(projects.get(i).getClientProjectId(),
							null, 0, "true", null, null);
//				automationReportBeanDiff = reportsService.getAutomationReport(projects.get(i).getClientProjectId(),
//						null, 0, "true", null, previousWeekDate);
					progressReportData = reportsService.getAutomationProgress(projects.get(i).getClientProjectId(),
							"true", previousDate, thisWeekDate);

					String tableData = "";
					if (automationReportBean.getTotalTestCaseIds().size() > 0) {
						MailData += "<h3>Project: " + projects.get(i).getName() + "</h3>";

//					######################################################

						int totalTestCaseDiff = progressReportData.getAddedTestCaseIds().size()
								- progressReportData.getDeletedTestCaseIds().size();

						int totalTestCaseAdded = progressReportData.getAddedTestCaseIds().size()
								+ progressReportData.getNewlyAddedAndDeletedTestCaseIds().size();

						int totalTestCaseDeleted = progressReportData.getDeletedTestCaseIds().size()
								+ progressReportData.getNewlyAddedAndDeletedTestCaseIds().size();

//					Added Tc Users

						String totalTestCaseAddedUsers = "";
						Map<Integer, String> totalTestCaseAddedUsersMap = progressReportData.getAddedTestCaseIdsMap();
						Map<Integer, String> totalNewTestCaseAddedAndRemovedUsersMap = progressReportData
								.getNewlyAddedAndDeletedTestCaseIdsMap();

						List<String> totalTestCaseAddedUsersList = new ArrayList<String>();
						for (Map.Entry<Integer, String> entry : totalTestCaseAddedUsersMap.entrySet()) {
							totalTestCaseAddedUsersList.add(entry.getValue());
						}

						for (Map.Entry<Integer, String> entry : totalNewTestCaseAddedAndRemovedUsersMap.entrySet()) {
							totalTestCaseAddedUsersList.add(entry.getValue());
						}

						Set<String> totalTestCaseAddedUsersSet = new LinkedHashSet<>(totalTestCaseAddedUsersList);
						for (String set : totalTestCaseAddedUsersSet) {
							totalTestCaseAddedUsers += set + ": <span style=\"color: green;\"> &#x25B2;"
									+ Collections.frequency(totalTestCaseAddedUsersList, set) + "</span> &nbsp;";
						}

//					Deleted Tc Users

						String totalTcDeletedUsers = "";
						Map<Integer, String> totalTcDeletedUsersMap = progressReportData.getDeletedTestCaseIdsMap();
						Map<Integer, String> totalNewTestCaseAddedAndRemovedUsersMapDelete = progressReportData
								.getNewlyAddedAndDeletedTestCaseIdsMap();

						List<String> totalTcDeletedUsersList = new ArrayList<String>();
						for (Map.Entry<Integer, String> entry : totalTcDeletedUsersMap.entrySet()) {
							totalTcDeletedUsersList.add(entry.getValue());
						}

						for (Map.Entry<Integer, String> entry : totalNewTestCaseAddedAndRemovedUsersMapDelete
								.entrySet()) {
							totalTcDeletedUsersList.add(entry.getValue());
						}

						Set<String> totalTcDeletedUsersSet = new LinkedHashSet<>(totalTcDeletedUsersList);
						for (String set : totalTcDeletedUsersSet) {
							totalTcDeletedUsers += set + ": <span style=\"color: red;\"> &#x25BC;"
									+ Collections.frequency(totalTcDeletedUsersList, set) + "</span> &nbsp;";
						}

//					Automation Done Users

						String totalMappingAddedUsers = "";
						Map<Integer, String> totalMappingAddedUsersMap = progressReportData
								.getAddedTestCaseMappingIdsMap();

						List<String> totalMappingAddedUsersList = new ArrayList<String>();
						for (Map.Entry<Integer, String> entry : totalMappingAddedUsersMap.entrySet()) {
							totalMappingAddedUsersList.add(entry.getValue());
						}

						Set<String> totalMappingAddedUsersSet = new LinkedHashSet<>(totalMappingAddedUsersList);
						for (String set : totalMappingAddedUsersSet) {
							totalMappingAddedUsers += set + ": <span style=\"color: green;\"> &#x25B2;"
									+ Collections.frequency(totalMappingAddedUsersList, set) + "</span> &nbsp;";
						}

//					Automation Removed Users

						String totalMappingDeletedUsers = "";
						Map<Integer, String> totalMappingDeletedUsersMap = progressReportData
								.getDeletedTestCaseMappingIdsMap();

						List<String> totalMappingDeletedUsersList = new ArrayList<String>();
						for (Map.Entry<Integer, String> entry : totalMappingDeletedUsersMap.entrySet()) {
							totalMappingDeletedUsersList.add(entry.getValue());
						}

						Set<String> totalMappingDeletedUsersSet = new LinkedHashSet<>(totalMappingDeletedUsersList);
						for (String set : totalMappingDeletedUsersSet) {
							totalMappingDeletedUsers += set + ": <span style=\"color: red;\"> &#x25BC;"
									+ Collections.frequency(totalMappingDeletedUsersList, set) + "</span> &nbsp;";
						}

//					######################################################

						if (loopi == 1) {
							totalTestCaseAddedUsers = "";
							totalTcDeletedUsers = "";
							totalMappingAddedUsers = "";
							totalMappingDeletedUsers = "";
						} else {
							if (totalTestCaseAddedUsers.length() > 1) {
								totalTestCaseAddedUsers = "(" + totalTestCaseAddedUsers + ")";
							}
							if (totalTcDeletedUsers.length() > 1) {
								totalTcDeletedUsers = "(" + totalTcDeletedUsers + ")";
							}
							if (totalMappingAddedUsers.length() > 1) {
								totalMappingAddedUsers = "(" + totalMappingAddedUsers + ")";
							}
							if (totalMappingDeletedUsers.length() > 1) {
								totalMappingDeletedUsers = "(" + totalMappingDeletedUsers + ")";
							}
						}

						tableData += "<p style=\"font-family: Arial, Helvetica, sans-serif;\"> From #dateRange#,</p> <br />"

								.replace("#dateRange#", "<span style=\"color: #008080\">" + previousDate
										+ "</span> to <span style=\"color: #008080\">" + "Today" + "</span>");

						// Adding the bar chart for every project
						tableData += TEMPLATE_GRAPH
								.replace("#TotalTestCase#", "" + automationReportBean.getTotalTestCaseIds().size())
								.replace("#TotalTestCasePercent#", "100%")

								.replace("#AutomatableTestCase#",
										"" + automationReportBean.getTotalAutomatableTestCaseIds().size())
								.replace("#AutomatableTestCasePercent#",
										"" + String.format("%.02f", automationReportBean.getAutomatablePercentage())
												+ "%")

								.replace(
										"#AutoDoneTestCase#", ""
												+ automationReportBean.getAutomatedTestCaseIds().size())
								.replace(
										"#AutoDoneTestCasePercent#", ""
												+ String.format("%.02f",
														((float) (automationReportBean.getAutomatedTestCaseIds().size()
																* 100)
																/ (automationReportBean.getTotalTestCaseIds().size())))
												+ "%")

								.replace("#AutoPendingTestCase#",
										"" + automationReportBean.getPendingAutomatedTestCaseIds().size())
								.replace(
										"#AutoPendingTestCasePercent#", ""
												+ String.format("%.02f",
														((float) (automationReportBean.getPendingAutomatedTestCaseIds()
																.size() * 100)
																/ (automationReportBean.getTotalTestCaseIds().size())))
												+ "%")

								.replace("#NonAutoTestCase#",
										"" + automationReportBean.getTotalManualTestCaseIds().size())
								.replace("#NonAutoTestCasePercent#",
										"" + String.format("%.02f", automationReportBean.getManualPercentage()) + "%");

						tableData += TEMPLATE_TABLE

								.replace("#TotalTcAdded#", "" + totalTestCaseAdded)
								.replace("#TotalTcAddedUsers#", "" + totalTestCaseAddedUsers)
								.replace("#TotalTcDeleted#", "" + totalTestCaseDeleted)
								.replace("#TotalTcDeletedUsers#", "" + totalTcDeletedUsers)
								.replace("#TotalMappingAdded#",
										"" + progressReportData.getAddedTestCaseMappingIds().size())
								.replace("#TotalMappingAddedUsers#", "" + totalMappingAddedUsers)
								.replace("#TotalMappingDeleted#",
										"" + progressReportData.getDeletedTestCaseMappingIds().size())
								.replace("#TotalMappingDeletedUsers#", "" + totalMappingDeletedUsers)

								.replace("#TotalTestCaseColor#",
										totalTestCaseDiff >= 0 ? totalTestCaseDiff == 0 ? "#404040" : "green" : "red")
								.replace("#TotalTestCase#", "" + automationReportBean.getTotalTestCaseIds().size()
										+ " ("
										+ (totalTestCaseDiff >= 0 ? totalTestCaseDiff == 0 ? "&#x223C;" : "&#x25B2;"
												: "&#x25BC;")
										+ totalTestCaseDiff + ")")
								.replace("#TotalTestCasePercent#", "100%")

								.replace("#AutomatableTestCase#",
										"" + automationReportBean.getTotalAutomatableTestCaseIds().size())
								.replace("#AutomatableTestCasePercent#",
										"" + String.format("%.02f", automationReportBean.getAutomatablePercentage())
												+ "%")

								.replace(
										"#AutoDoneTestCase#", ""
												+ automationReportBean.getAutomatedTestCaseIds().size())
								.replace(
										"#AutoDoneTestCasePercent#", ""
												+ String.format("%.02f",
														((float) (automationReportBean.getAutomatedTestCaseIds().size()
																* 100)
																/ (automationReportBean.getTotalTestCaseIds().size())))
												+ "%")

								.replace("#AutoPendingTestCase#",
										"" + automationReportBean.getPendingAutomatedTestCaseIds().size())
								.replace(
										"#AutoPendingTestCasePercent#", ""
												+ String.format("%.02f",
														((float) (automationReportBean.getPendingAutomatedTestCaseIds()
																.size() * 100)
																/ (automationReportBean.getTotalTestCaseIds().size())))
												+ "%")

								.replace("#NonAutoTestCase#",
										"" + automationReportBean.getTotalManualTestCaseIds().size())
								.replace("#NonAutoTestCasePercent#",
										"" + String.format("%.02f", automationReportBean.getManualPercentage()) + "%");

					} else {
//					MailData += "<br /><h4>No Testcases available for " + projects.get(i).getName() + "</h4><br />";
					}
					MailData += tableData;
				}
				MailData += " <p>Note: Please do not reply on this mail.</p> <h3>Thanks</h3> "
						+ "<h2 style=\"color: #008080\">© Pintailer™ 2019</h2>";

				HtmlToImage imageGenerator = new HtmlToImage();
				imageGenerator.loadHtml(MailData);
				imageGenerator.setSize(new Dimension(1366, 768));
				imageGenerator.saveAsImage("pintailer-coverage" + organization + ".png");
				imageGenerator.saveAsHtmlWithMap("pintailer-coverage" + organization + ".html",
						"pintailer-coverage" + organization + ".png");

				try (BufferedReader br = new BufferedReader(
						new FileReader("pintailer-coverage" + organization + ".html"))) {
					StringBuilder sb = new StringBuilder();
					String line = br.readLine();

					while (line != null) {
						sb.append(line);
						sb.append(System.lineSeparator());
						line = br.readLine();
					}
					MailData = sb.toString();
				}

				for (int user = 0; user < userList.length; user++) {
					sendReportsMail(userList[user], REPORTS_SUBJECT.replace("#orgName#", organization), MailData,
							organization);
				}

			}
		}
	}

}
