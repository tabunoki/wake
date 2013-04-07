package com.binarysprite.wake;


/**
 * 
 * @author Tabunoki
 *
 */
public enum WebBuilderMode {
	
	BUILD {

		@Override
		public void handle(WebBuilder builder, WebBuilderParam param) {
			builder.build(this, param);
		}
	},
	LIST {

		@Override
		public void handle(WebBuilder builder, WebBuilderParam param) {
			builder.list(this, param);
		}
	};
	
	public abstract void handle(WebBuilder builder, WebBuilderParam param);
}