package com.hiperboot.util;

public enum DateFormatType {
    ISO_DATE,            // YYYY-MM-DD
    ISO_DATETIME,        // YYYY-MM-DDThh:mm:ss
    ISO_DATETIME_TZ,     // YYYY-MM-DDThh:mm:ss+hh:mm
    ISO_DATETIME_UTC,    // YYYY-MM-DDThh:mm:ssZ
    RFC_1123,            // EEE, dd MMM yyyy HH:mm:ss zzz
    EPOCH_SECONDS,       // Unix timestamp in seconds
    EPOCH_MILLISECONDS,  // Unix timestamp in milliseconds
    SQL_DATETIME,         // Format like "YYYY-MM-DD HH:MM:SS.s"
    UNIDENTIFIED         // Unidentified Format
}