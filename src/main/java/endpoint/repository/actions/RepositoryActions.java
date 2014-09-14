package endpoint.repository.actions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import endpoint.repository.IdRef;
import endpoint.repository.Repository;
import endpoint.repository.response.HttpResponse;
import endpoint.repository.response.JsonResponse;
import endpoint.utils.JsonUtils;
import endpoint.utils.ThrownExceptionsUtils;

public class RepositoryActions {

	public static HttpResponse execute(Repository r, IdRef<?> actionId, Method action, Map<String, String> params) {
		try {
			@SuppressWarnings("unchecked")
			Class<? extends Action<?>> actionClazz = (Class<? extends Action<?>>) action.getDeclaringClass();

			Action<?> actionInstance = actionClazz.newInstance();
			actionInstance.setRepository(r);

			IdRef<?> id = actionId;
			// if (actionId.asLong() == null) {
			// assert EndpointScanner.isOverCollection(action);
			// id = actionId.getParentId();
			// } else {
			// assert !EndpointScanner.isOverCollection(action);
			// id = actionId;
			// }
			Object[] arguments = getActionArguments(action, params, id);

			Object ret = action.invoke(actionInstance, arguments);
			if (action.getReturnType().equals(Void.TYPE)) {
				return null;
			}
			if (HttpResponse.class.isInstance(ret)) {
				return (HttpResponse) ret;
			}

			return new JsonResponse(JsonUtils.to(ret));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
			throw ThrownExceptionsUtils.handle(e);
		}
	}

	private static Object[] getActionArguments(Method action, Map<String, String> params, IdRef<?> id) {
		if (action.getParameterTypes().length == 0) {
			return new Object[] {};
		}
		if (action.getParameterTypes().length == 2) {
			return new Object[] { id, params };
		}

		if (IdRef.class.equals(action.getParameterTypes()[0])) {
			return new Object[] { id };
		}

		return new Object[] { params };
	}
}
