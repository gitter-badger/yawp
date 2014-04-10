package endpoint.tools;

import java.util.Arrays;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

public class ReIndex {

	private String kind;
	private String[] properties;

	public ReIndex(String kind, String[] properties) {
		this.kind = kind;
		this.properties = properties;
	}

	public static void now(String kind, String... properties) {
		ReIndex reIndex = new ReIndex(kind, properties);
		reIndex.now();
	}

	public static ReIndex parse(String path) {
		String[] parts = path.split("/");

		if (!isValidPath(parts)) {
			throw new RuntimeException("Invalid ReIndex path: " + path + " - use: /kind/property1/property2/.../propertyN");
		}

		String kind = parts[1];
		String[] properties = Arrays.copyOfRange(parts, 2, parts.length);

		return new ReIndex(kind, properties);
	}

	private static boolean isValidPath(String[] parts) {
		return parts.length >= 2;
	}

	public void now() {
		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query(kind);
		PreparedQuery pq = datastoreService.prepare(q);

		Iterable<Entity> result = pq.asIterable();
		for (Entity entity : result) {
			for (int i = 0; i < properties.length; i++) {
				String property = properties[i];
				entity.setProperty(property, entity.getProperty(property));
			}
			datastoreService.put(entity);
		}
	}

}
