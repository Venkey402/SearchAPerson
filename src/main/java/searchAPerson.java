import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.Test;

public class searchAPerson {

	@Test
	public void test() throws InterruptedException, IOException {
		String district = "VISAKHAPATNAMadfadsf";
		String surname = "boddana";
		List<String> list_RC = new ArrayList<String>();
		String userdir = System.getProperty("user.dir");
		System.setProperty("webdriver.chrome.driver",
				userdir + "/SearchAPerson/src/main/resources/chromedriver.exe");
		WebDriver driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.get("https://ysrbima.ap.gov.in/new/SearchName.aspx");
		driver.findElement(By.id("ContentPlaceHolder1_RadioButtonList1_2")).click();
		Thread.sleep(3000);
		Select select_district = new Select(driver.findElement(By.id("ContentPlaceHolder1_Distric")));
		select_district.selectByVisibleText(district);
		Thread.sleep(3000);
		Select select_mandal2 = new Select(driver.findElement(By.name("ctl00$ContentPlaceHolder1$mandal")));

		List<WebElement> mandalsInDistrict_web = select_mandal2.getOptions();
		List<String> mandalsInDistrict = new ArrayList<String>();

		for (int m = 0; m < mandalsInDistrict_web.size(); m++) {
			mandalsInDistrict.add(mandalsInDistrict_web.get(m).getText());
		}

		for (String mandalOption : mandalsInDistrict) {
			if (!mandalOption.startsWith("--Select")) {
				Select select_mandal = new Select(driver.findElement(By.name("ctl00$ContentPlaceHolder1$mandal")));
				select_mandal.selectByVisibleText(mandalOption);
				Thread.sleep(3000);

				Select select_secrt = new Select(driver.findElement(By.name("ctl00$ContentPlaceHolder1$village")));
				List<WebElement> villageInMandals_web = select_secrt.getOptions();
				List<String> villageInMandals = new ArrayList<String>();

				for (int v = 0; v < villageInMandals_web.size(); v++) {
					villageInMandals.add(villageInMandals_web.get(v).getText());
				}

				for (String villageOption : villageInMandals) {
					if (!villageOption.startsWith("--Select")) {
						Select select_secrt2 = new Select(
								driver.findElement(By.name("ctl00$ContentPlaceHolder1$village")));
						select_secrt2.selectByVisibleText(villageOption);
						Thread.sleep(3000);
						driver.navigate().refresh();
						Thread.sleep(2000);
						WebElement surnameTxt = driver.findElement(By.name("ctl00$ContentPlaceHolder1$Textnamesearch"));
						surnameTxt.clear();
						surnameTxt.sendKeys(surname);
						JavascriptExecutor js = (JavascriptExecutor) driver;
						js.executeScript(
								"document.getElementById('ContentPlaceHolder1_LinkButton1').scrollIntoView(true);");
						js.executeScript("arguments[0].click();",
								driver.findElement(By.id("ContentPlaceHolder1_LinkButton1")));
						Thread.sleep(3000);
						List<WebElement> details_rc = driver
								.findElements(By.xpath("//table[@id='ContentPlaceHolder1_gridview1']//tr/td[2]"));

						for (WebElement ele : details_rc) {
							list_RC.add(ele.getText());
						}
						driver.navigate().refresh();
					}
				}
			}
		}
		search_rc(list_RC, driver);

	}

	public void search_rc(List<String> list_RC, WebDriver driver) throws InterruptedException, IOException {
		for (String rc_no : list_RC) {
			String userdir = System.getProperty("user.dir");
			String file = userdir+"\\src\\main\\resources\\search_output.csv";
			driver.navigate().to("https://aepos.ap.gov.in/ePos/SRC_Trans_Int.jsp");
			driver.findElement(By.id("rcno")).sendKeys(rc_no);
			driver.findElement(By.xpath("//button[text()='Submit']")).click();
			FileWriter fw = new FileWriter(file, true);
			Thread.sleep(3000);
			List<WebElement> family_details = driver.findElements(By.xpath("//*[@id='detailsED']//table[1]//tr"));
			// System.out.println("No.of Family members"+(family_details.size()-3));
			for (int i = 4; i <= family_details.size(); i++) {

				WebElement member = driver.findElement(By.xpath("//*[@id='detailsED']//table[1]//tr[" + i + "]/td[2]"));
				WebElement age = driver.findElement(By.xpath("//*[@id='detailsED']//table[1]//tr[" + i + "]/td[3]"));
				WebElement relation = driver
						.findElement(By.xpath("//*[@id='detailsED']//table[1]//tr[" + i + "]/td[5]"));

				if (relation.getText().contains("DAUGHTER")) {
					// the true will append the new data
					fw.write(rc_no + "," + member.getText() + "," + age.getText() + "\r");// appends the string to the
																							// file
					System.out.println("RC No:" + rc_no + ", " + "Member:" + member.getText() + ", " + "Age:"
							+ age.getText() + ", " + "Village:" + "" + ", " + "Mandal:" + "" + ", " + "District:" + "");
				}
			}
			fw.close();
		}
	}
}
