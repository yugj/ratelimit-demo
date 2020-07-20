import org.junit.Test;
import org.springframework.web.client.RestTemplate;

/**
 * @author yugj
 * @date 2019/8/20 下午6:29.
 */
public class FlowTest {

    RestTemplate template = new RestTemplate();

    @Test
    public void test1() throws InterruptedException {

        while (true) {
            Thread.sleep(100L);

            try {
                String hell = template.getForObject("http://localhost:9000/hello/hell?uipp=x", String.class);
                System.out.println("t1:" + hell);
            } catch (Exception e) {
                System.out.println("429");
            }

        }
    }


    @Test
    public void test2() throws InterruptedException {
        while (true) {
            Thread.sleep(100L);
            try {
                String hell = template.getForObject("http://localhost:8888/hello/hell?uipp=x", String.class);
                System.out.println("t2:" + hell);
            } catch (Exception e) {
                System.out.println("429");
            }

        }
    }

    @Test
    public void test3() {

    }
}
