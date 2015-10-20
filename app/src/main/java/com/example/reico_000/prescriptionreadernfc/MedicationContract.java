package com.example.reico_000.prescriptionreadernfc;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Yosua Susanto on 19/10/2015.
 */
public final class MedicationContract {

        /**
         * The authority of the Medication provider.
         */
        public static final String AUTHORITY =
                "com.example.reico_000.prescriptionreadernfc.provider";
        /**
         * The content URI for the top-level
         * Medication authority.
         */
        public static final Uri CONTENT_URI =
                Uri.parse("content://" + AUTHORITY);

        /**
         * Constants for the Medications table
         * of the Medication provider.
         */
        public static final class Medications
                implements BaseColumns {
            /**
            * The content URI for this table.
            */
            public static final Uri CONTENT_URI =
                    Uri.withAppendedPath(
                            MedicationContract.CONTENT_URI,
                            "medicationTable");
            /**
             * The mime type of a directory of items.
             */
            public static final String CONTENT_TYPE =
                    ContentResolver.CURSOR_DIR_BASE_TYPE +
                            "/vnd.example.reico_000.prescriptionreadernfc.provider.medicationTable";
            /**
             * The mime type of a single item.
             */
            public static final String CONTENT_MED_TYPE =
                    ContentResolver.CURSOR_ITEM_BASE_TYPE +
                            "/vnd.example.reico_000.prescriptionreadernfc.provider.medicationTable";
            /**
             * A projection of all columns
             * in the items table.
             */
            public static final String KEY_ID = "_id";
            public static final String KEY_BRAND_NAME = "BrandName";
            public static final String KEY_GENERIC_NAME = "GenericName";
            public static final String KEY_DOSAGE_FORM = "DosageForm";
            public static final String KEY_PER_DOSAGE = "PerDosage";
            public static final String KEY_TOTAL_DOSAGE = "TotalDosage";
            public static final String KEY_CONSUMPTION_TIME = "ConsumptionTime";
            public static final String KEY_PATIENT_ID = "PatientID";
            public static final String KEY_ADMINISTRATION = "Administration";
            public static final String[] PROJECTION_ALL = new String[] {KEY_ID, KEY_BRAND_NAME, KEY_GENERIC_NAME, KEY_DOSAGE_FORM,
                    KEY_PER_DOSAGE, KEY_TOTAL_DOSAGE, KEY_CONSUMPTION_TIME, KEY_PATIENT_ID, KEY_ADMINISTRATION};
            /**
             * The default sort order for
             * queries containing NAME fields.
             */
            public static final String SORT_ORDER_DEFAULT =
                    KEY_ID + " ASC";
        }

        /**
         * Constants for the Consumption table of the
         * Medication provider.
         */
        public static final class Consumption
                implements BaseColumns {
            /**
             * The content URI for this table.
             */
            public static final Uri CONTENT_URI =
                    Uri.withAppendedPath(
                            MedicationContract.CONTENT_URI,
                            "consumptionTable");
            /**
             * The mime type of a directory of items.
             */
            public static final String CONTENT_TYPE =
                    ContentResolver.CURSOR_DIR_BASE_TYPE +
                            "/vnd.example.reico_000.prescriptionreadernfc.provider/consumptionTable";
            /**
             * The mime type of a single item.
             */
            public static final String CONTENT_CONSUMPTION_TYPE =
                    ContentResolver.CURSOR_ITEM_BASE_TYPE +
                            "/vnd.example.reico_000.prescriptionreadernfc.provider/consumptionTable";
            /**
             * A projection of all columns
             * in the items table.
             */
            public static final String KEY_ID = "_id";
            public static final String KEY_MEDICATION_ID = "MedicationID";
            public static final String KEY_CONSUMED_AT = "ConsumedAt";
            public static final String[] PROJECTION_ALL = new String[] {KEY_ID, KEY_MEDICATION_ID, KEY_CONSUMED_AT};
            /**
             * The default sort order for
             * queries containing NAME fields.
             */
            public static final String SORT_ORDER_DEFAULT =
                    KEY_ID + " ASC";
        }

}

//        /**
//         * This interface defines common columns
//         * found in multiple tables.
//         */
//        public static interface CommonColumns
//                extends BaseColumns { … }


