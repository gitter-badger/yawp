package endpoint.repository.hooks;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import endpoint.repository.SimpleObject;
import endpoint.utils.DateUtils;
import endpoint.utils.EndpointTestCase;

public class HookTest extends EndpointTestCase {

	@Test
	public void testAfterSave() {
		SimpleObject object = new SimpleObject(1, 1l, 1.1, true, DateUtils.toTimestamp("2013/12/26 23:55:01"), "object1");
		r.saveWithHooks(object);

		assertEquals("just rock it", object.getChangeInCallback());
	}

	@Test
	public void testAllTargetsHook() {
		Product product = saveProduct("xpto");
		assertEquals("xpto GenericHook touch", product.getName());
	}

	@Test
	public void testBeforeQuery() {
		saveProduct("xpto");
		saveProduct("abcd");

		List<Product> products = r.queryWithHooks(Product.class).list();
		assertEquals(1, products.size());
		assertEquals("xpto", products.get(0).getName());
	}

	private Product saveProduct(String name) {
		Product product = new Product();
		product.setName(name);
		r.saveWithHooks(product);
		return product;
	}

}