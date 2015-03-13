all: compile
clean:
	@-rm -rf out/*
compile: clean
	@echo Compile
	@javac -d out -classpath lib/commons-cli-1.2/commons-cli-1.2.jar:lib/Netty/netty-all-4.0.25.Final.jar src/*/*
	@echo Build
	@jar -cvfm httpd.jar MANIFEST.MF -C out/ .
	@echo Success
