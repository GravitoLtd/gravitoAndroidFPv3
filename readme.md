# Gravito First-Party API Integration

This Kotlin-based app demonstrates the use of the Gravito First-Party API to persist consent in the Gravito backend.

## Project Structure

In the attached project, there is an `ApiClient.kt` file which contains the abstracted logic for communication with the first-party API.

## Integration Steps

1. **Add OkHttp Package**: Integrate the OkHttp package in your Android app. You can add the following dependency in your `build.gradle` file:
    ```groovy
    implementation 'com.squareup.okhttp3:okhttp:4.9.1'
    ```

2. **Copy ApiClient.kt**: Directly copy the `ApiClient.kt` file into your project.

3. **Usage in MainActivity**: In `MainActivity.kt`, we have demonstrated the use of the `GravitoFPApiClient` class.

## Example Usage

To use the `GravitoFPApiClient` class, you need to pass the context, parent domain and origin while creating an instance of the class. The parent domain will be something like `yourdomain.com` and the origin will be `app.yourdomain.com`. You need to get this domain validated with Gravito first.

The GravitoFPApiClient class takes three arguments: context, which is required to store data in shared preferences, parentDomain, and origin.



```kotlin
val gravitoClient = GravitoFPApiClient(context, "yourdomain.com", "app.yourdomain.com")
```

Make sure to replace `yourdomain.com` and `app.yourdomain.com` with your actual domain and origin.

## Validation

Ensure that your domain is validated with Gravito before using the API.

For more details, refer to the Gravito documentation.
