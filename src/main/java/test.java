import org.testng.annotations.Test;

public class test {

	@Test
	public void aa()
	{
		String userdir = System.getProperty("user.dir");
		System.out.println(userdir);
	}
}
