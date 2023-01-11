FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:imx8mmevk = " file://sd-boot-cmd.patch;patchdir=${WORKDIR}"

