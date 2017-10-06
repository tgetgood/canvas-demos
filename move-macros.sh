for x in $(find src | egrep cljc?$ | sed 's/src\///')
	do cp src/$x resources/public/js/compiled/out/$x
done
