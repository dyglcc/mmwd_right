/**
 * EventType is an enum field of various Android event type to track.
 */

package com.adhoc.adhocsdk;

public enum EventType {
	INSTALL_REFERRER, // New installation of application
	ACTIVITY_START,  // Activity start event
	ACTIVITY_STOP,  // Activity stop event
    GET_EXPERIMENT_FLAGS,  // App getting experiment flags
    REPORT_STAT,  // 汇报统计
}
