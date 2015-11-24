package com.addongaming.overkill.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.addongaming.hcessentials.data.Enchantable;
import com.addongaming.hcessentials.utils.Utils;

public class StackSizeChanger {

	public void alterArmourWeaponsStackSize(int maxSize) {
		try {
			System.out.printf("Changing Armour and Weapons Max size to {0}",
					maxSize);
			Class<?> craftItem = Class.forName("net.minecraft.server."
					+ Utils.getServerVersion() + "Item");
			Field stackField = craftItem.getDeclaredField("maxStackSize");
			stackField.setAccessible(true);
			Method registry = getMethod(craftItem, craftItem,
					new Class<?>[] { int.class });
			for (Enchantable enchantable : Enchantable.values()) {
				Object itemField = registry.invoke(null, enchantable
						.getMaterial().getId());
				if (itemField != null) {
					stackField.setInt(itemField, maxSize);
				} else {
					System.err.println("Couldn't get the item.");
				}
			}
			stackField.setAccessible(false);
			System.out.println("Finished changing armour and weapons size.");
		} catch (Exception e) {
			System.err.println("Error altering max stack size.");
			e.printStackTrace(System.err);
		}
	}

	private Method getMethod(Class<?> mainClass, Class<?> returnType,
			Class<?>[] parameters) {
		for (Method method : mainClass.getMethods()) {
			if (method.getReturnType() == returnType
					&& method.getParameterTypes().length == parameters.length) {
				if (hasSameParameters(parameters, method.getParameterTypes()))
					return method;
			}
		}
		return null;
	}

	private boolean hasSameParameters(Class<?>[] params1, Class<?>... params2) {
		for (int i = 0; i < params1.length; i++) {
			if (params1[i] != params2[i])
				return false;
		}
		return true;
	}
}
