package testcases;
import org.testng.Assert; import org.testng.annotations.DataProvider; import org.testng.annotations.Test; import base.BaseClass; import pages.LoginPage; import utilities.ExcelUtils;
public class LoginTest extends BaseClass {
 @DataProvider(name="loginData") 
 public Object[][] data()
 {
	 return ExcelUtils.getData("Login").toArray(new Object[0][]);
 }
 @Test(dataProvider="loginData",  groups = {"ST", "FT", "IT", "SYS"})
 public void loginTest(String id,String type,String email,String password,String expected){
	 LoginPage p=new LoginPage(driver,Long.parseLong(prop.getProperty("explicitWait","10")));
	 p.open();
	 p.login(email,password);
	 if("Valid".equalsIgnoreCase(type))Assert.assertTrue(p.isLoggedIn(),id+" expected login success; error: "+p.getError());
	 else{
		 Assert.assertFalse(p.isLoggedIn(),id+" invalid login should fail");
		 Assert.assertTrue(!p.getError().isEmpty(),id+" expected error text");
		 }
	 }
}