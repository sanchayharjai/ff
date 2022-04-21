package com.project.website.Model;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.UUID;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.HibernateException;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class MyGenerator implements IdentifierGenerator {
	public static final String generateId = "myGenerator";
	private SecureRandom rand = new SecureRandom();

//	 
//		@Id
//		@GeneratedValue(generator = MyGenerator.generatorName)
//	    @GenericGenerator(name = MyGenerator.generatorName, strategy = "com.project.website.Model.MyGenerator")
	@Override
	public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object object)
			throws HibernateException {
		return Long.valueOf((this.rand.nextInt() & Integer.MAX_VALUE))+Long.valueOf((this.rand.nextInt() & Integer.MAX_VALUE))+Long.valueOf((this.rand.nextInt() & Integer.MAX_VALUE))+Long.valueOf((this.rand.nextInt() & Integer.MAX_VALUE))+Long.valueOf((this.rand.nextInt() & Integer.MAX_VALUE));
		// or any other logic you'd like for generating unique IDs
	}

}
