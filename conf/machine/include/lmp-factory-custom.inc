
# Use sdcard as root device by default
OSTREE_KERNEL_ARGS:imx8mm-lpddr4-evk = "console=tty1 console=ttymxc1,115200 earlycon=ec_imx6q,0x30890000,115200 root=/dev/mmcblk1p2 rootfstype=ext4"

# Add edge

PREFERRED_RPROVIDER_kubelet = "kubelet"
PREFERRED_RPROVIDER_kubernetes-cni = "cni"


EDGE_BASE_REQUIRED = " \
edge-core \
identity-tool \
path-set \
edge-version \
"

EDGE_BASE_OPTIONAL = " \
mbed-fcce \
"

EDGE_PROTOCOL_TRANSLATION = " \
edge-examples \
"

EDGE_SYSTEMS_MANAGEMENT = "\
edge-proxy \
info-tool \
testnet \
edge-terminal \
fluentbit \
maestro \
"

EDGE_CONTAINER_ORCHESTRATION = " \
kubelet \
edge-proxy \
"

PARSEC_SERVICE = " \
parsec-tool \
parsec-service \
"
TOOLCHAIN:pn-parsec-service = "gcc"

CORE_IMAGE_BASE_INSTALL:append = " \
${EDGE_BASE_REQUIRED} \
${EDGE_PROTOCOL_TRANSLATION} \
${EDGE_SYSTEMS_MANAGEMENT} \
${EDGE_CONTAINER_ORCHESTRATION} \
${PARSEC_SERVICE} \
"

