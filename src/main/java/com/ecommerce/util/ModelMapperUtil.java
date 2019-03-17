package com.ecommerce.util;

import org.modelmapper.ModelMapper;

public class ModelMapperUtil {
	private static final ModelMapper modelMapper = new ModelMapper();

	public static <T> Object map(Object object, Class<T> classType) {
		// disabling the ambiguity here.
		modelMapper.getConfiguration().setAmbiguityIgnored(true);

		return modelMapper.map(object, classType);
	}
}
