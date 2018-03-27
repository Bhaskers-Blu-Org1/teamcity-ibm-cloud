import org.junit.jupiter.api.*;

import ibm.buildServer.clouds.softlayer.API.ImageList;

import java.util.TreeMap;
import java.util.List;

import com.softlayer.api.*;
import com.softlayer.api.service.Account;
import com.softlayer.api.service.virtual.guest.block.device.template.*;

@DisplayName("ImageList Tests")
public class ImageListTest {
	
	@BeforeAll
    static void beforeAll() {
        System.out.println("Before all test methods");
    }
	
	@Test
	@DisplayName("First test")
	public void testSize()
	{
		System.out.println("First test method");
	}

}
