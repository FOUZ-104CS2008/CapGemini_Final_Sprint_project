package pages;
import java.time.Duration; import org.openqa.selenium.By; import org.openqa.selenium.WebDriver; import org.openqa.selenium.support.ui.ExpectedConditions; import org.openqa.selenium.support.ui.WebDriverWait;
public class LoginPage {
 private final WebDriver driver; 
 private final WebDriverWait wait; 
 private final By link=By.cssSelector("a.ico-login"), email=By.id("Email"), password=By.id("Password"), button=By.cssSelector("input.login-button"), logout=By.cssSelector("a.ico-logout"), summary=By.cssSelector(".validation-summary-errors");
 public LoginPage(WebDriver d,long seconds){
	 driver=d;
	 wait=new WebDriverWait(d,Duration.ofSeconds(seconds));
 }
 public void open(){
	 wait.until(ExpectedConditions.elementToBeClickable(link)).click();
 wait.until(ExpectedConditions.visibilityOfElementLocated(email));
 }
 public void login(String mail,String pass){
	 driver.findElement(email).clear();
	 driver.findElement(email).sendKeys(mail);
	 driver.findElement(password).clear();
	 driver.findElement(password).sendKeys(pass);
	 driver.findElement(button).click();}
 public boolean isLoggedIn(){
	 try{
		 return wait.until(ExpectedConditions.visibilityOfElementLocated(logout)).isDisplayed();
		 }catch(Exception e){
			 return false;}}
 public String getError(){try{return driver.findElement(summary).getText().trim();
 }catch(Exception e){
	 return "";}}
}