package com.rusudinu.consumer.model;

public enum ImageCategory {
    INGREDIENTS_LABEL,    // Image represents a picture of a product ingredients label
    NUTRITIONAL_TABLE,    // Image represents a picture of a product's nutritional table
    ALCOHOL,              // Product is an alcohol bottle
    CIGARETTES,           // Product is a cigarettes pack
    MAYBE,                // Image is blurry
    NONE                  // Image is not valid (e.g., image of a couch or wall)
}
