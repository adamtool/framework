## @author Manuel Gieseking

# dependencies (folders and repos should be equally ordered)
DEPENDENCIES_FOLDERS="libs,examples"
DEPENDENCIES_REPOS="https://github.com/adamtool/libs.git,https://github.com/adamtool/examples.git"
DEPENDENCIES_REV="HEAD,HEAD"
# the build target
FRAMEWORK_TARGETS = tools petrinetwithtransits
t=javac

# should be executed no matter if a file with the same name exists or not
.PHONY: pull_dependencies
.PHONY: rm_dependencies
.PHONY: tools
.PHONY: petrinetwithtransits
#.PHONY: javadoc
.PHONY: setJavac
.PHONY: setJar
.PHONY: setStandalone
.PHONY: setClean
.PHONY: setCleanAll
.PHONY: clean
.PHONY: clean-all
.PHONY: src_withlibs
.PHONY: src

define generate_src
	mkdir -p adam_src
	if [ $(1) = true ]; then\
		cp -R ./dependencies/libs ./adam_src/libs/; \
		rm -rf ./adam_src/libs/.git; \
	fi
	for i in $$(find . -type d \( -path ./benchmarks -o -path ./test/lib -o -path ./lib -o -path ./adam_src -o -path ./dependencies -o -path ./.git \) -prune -o -name '*' -not -regex ".*\(class\|qcir\|pdf\|tex\|apt\|dot\|jar\|ods\|txt\|tar.gz\|aux\|log\|res\|aig\|aag\|lola\|cex\|properties\|json\|xml\|out\|pnml\|so\)" -type f); do \
		echo "cp" $$i; \
		cp --parent $$i ./adam_src/ ;\
	done
	tar -zcvf adam_src.tar.gz adam_src
	rm -r -f ./adam_src
endef

# targets
all: $(FRAMEWORK_TARGETS)

pull_dependencies:
	./pull_dependencies.sh ${DEPENDENCIES_FOLDERS} ${DEPENDENCIES_REPOS} ${DEPENDENCIES_REV}

rm_dependencies:
	$(RM) -rf dependencies

tools:
	ant -buildfile ./tools/build.xml $(t)

petrinetwithtransits:
	ant -buildfile ./petrinetWithTransits/build.xml $(t)

setJavac:
	$(eval t=javac)

setStandalone:
	$(eval t=jar-standalone)

setClean:
	$(eval t=clean)

setCleanAll:
	$(eval t=clean-all)

clean: setClean $(FRAMEWORK_TARGETS)
	$(RM) -r -f deploy
	$(RM) -r -f javadoc

clean-all: setCleanAll $(FRAMEWORK_TARGETS)
	$(RM) -r -f deploy
	$(RM) -r -f javadoc

#javadoc:
#	ant javadoc

src_withlibs: clean-all
	$(call generate_src, true)

src: clean-all
	$(call generate_src, false)
