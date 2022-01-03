LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := librecovery
LOCAL_SRC_FILES := stub.c
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libssl_helper
LOCAL_SRC_FILES := stub.c
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libaddon_d
LOCAL_SRC_FILES := stub.c
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libinfo
LOCAL_SRC_FILES := stub.c
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libinstall
LOCAL_SRC_FILES := stub.c
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libbusybox
LOCAL_SRC_FILES := stub.c
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libremove
LOCAL_SRC_FILES := stub.c
include $(BUILD_SHARED_LIBRARY)

