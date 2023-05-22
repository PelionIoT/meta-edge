SUMMARY = "Adds the version number file (versions.json) for edge-info"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

# LmP Edge version is store in this file called BUILDMMU.txt
SRC_URI = "file://BUILDMMU.txt"

PR = "r0"

S = "${WORKDIR}"

FILES:${PN} = " \
/edge \
/edge/etc \
/edge/etc/versions.json \
"

do_compile() {
	BUILDMMU=$(cat ${S}/BUILDMMU.txt)
	VER_FILE=${S}/versions.json
	if [ -e "$VER_FILE" ] ; then
		rm "$VER_FILE"
	fi
	echo  "{" > "$VER_FILE"
	echo  "   "  \"version\" ":" \"${BUILDMMU}\" >> "$VER_FILE"
	echo  "}" >> "$VER_FILE"
}

do_install() {
	install -d ${D}/edge/etc
	install -m 0755 ${S}/versions.json ${D}/edge/etc/versions.json
}
