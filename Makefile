clean_build_native_image:
# 	./mvnw clean -Pnative native:compile
	./mvnw clean -Pnative native:compile -Darguments="--trace-class-initialization=java.awt.image.ColorModel" -Djava.awt.headless=true

run_native_image:
	./target/puml

clean_run:
	$(MAKE) clean_build_native_image
	$(MAKE) run_native_image