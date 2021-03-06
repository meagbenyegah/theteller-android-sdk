# theteller's Android Dev Kit

theteller's Android Dev Kit is a readymade UI that allows you to accept card and bank payments in your Android app.

<img alt="Screenshot of Dev Kit" src="https://firebasestorage.googleapis.com/v0/b/theteller-android-sdk.appspot.com/o/theteller-android-sdk-screenshots.png?alt=media&token=18ad0388-aa0e-438d-a7b1-5508fb3da676" width="1200"/>

## Before you begin


## Requirements


The minimum supported SDK version is 15

## Adding it to your project


**Step 1.** Add it in your root build.gradle at the end of repositories:
[![](https://jitpack.io/v/apeatu/theteller-android-sdk.svg)](https://jitpack.io/#apeatu/theteller-android-sdk)

    allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

**Step 2.** Add the dependency

    dependencies {
        implementation fileTree(dir: 'libs', include: ['*.jar'])
        androidTestCompile('com.android.support.test.espresso:espresso-core:2.2.2', {
            exclude group: 'com.android.support', module: 'support-annotations'
        })
        implementation 'com.android.support:appcompat-v7:25.4.0'
        implementation 'com.android.support:design:25.4.0'
        implementation 'com.android.support:support-v4:25.4.0'
        implementation 'com.squareup.picasso:picasso:2.5.2'
        implementation 'com.squareup.retrofit2:retrofit:2.4.0'
        implementation 'com.squareup.retrofit2:converter-gson:2.3.0'
        implementation 'com.squareup.okhttp3:logging-interceptor:3.8.0'
        implementation 'org.parceler:parceler-api:1.1.9'
        annotationProcessor 'org.parceler:parceler:1.1.9'
        implementation 'com.squareup.retrofit2:converter-scalars:2.1.0'
        implementation 'com.scottyab:aescrypt:0.0.1'
        testImplementation 'junit:junit:4.12'
        implementation 'com.android.support.constraint:constraint-layout:1.1.2'
	    implementation 'com.github.apeatu:theteller-android-sdk:1.0.5'
	}

**Step 3.** Add the required permission

Add the `READ_PHONE_PERMISSION` and `INTERNET` permissions to your android manifest

     <uses-permission android:name="android.permission.READ_PHONE_STATE" />
     <uses-permission android:name="android.Manifest.permission.READ_PHONE_STATE" />
     <uses-permission android:name="android.permission.INTERNET" />

> **REQUIRED PERMISSION**
> This library requires the **READ_PHONE_PERMISSION** to get the build number for fraud detection and flagging as recommended here https://developer.android.com/training/articles/user-data-ids.html#i_abuse_detection_detecting_high_value_stolen_credentials

## Usage

###  1. Create a `thetellerManager` instance
Set the api key and other required parameters. The `thetellerManager` accepts a mandatory instance of  the calling `Activity`

                new thetellerManager(this).setAmount(Long.parseLong(amount))
                    .setEmail(email)
                    .setfName(fName)
                    .setlName(lName)
                    .setMerchant_id(merchantId)
                    .setNarration(narration)
                    .setApiUser(apiUser)
                    .setApiKey(apiKey)
                    .setTxRef(txRef)
                    .set3dUrl(dUrl)
                    .acceptCardPayments(cardSwitch.isChecked())
                    .acceptGHMobileMoneyPayments(ghMobileMoneySwitch.isChecked())
                    .onStagingEnv(!isLiveSwitch.isChecked())
                    .initialize();

| function        | parameter           | type | required  |
| ------------- |:-------------:| -----:| -----:|
| setAmount(amount)      |  This is the amount to be charged from card/account | `long` | Required
| setfName(fName) | This is the first name of the card holder or the customer  | `String` | Required
| setlName(lName) | This is the last name of the card holder or the customer | `String` | Required
| setEmail(email) | This is the email address of the customer | `String` | Required
| setNarration(narration) | This is a custom description added by the merchant | `String` | Not Required
| setApiKey(apiKey) | Merchant's API key | `String` | Required
| setApiUser(apiUser) | Merchant's Api Username | `String` | Required
| setTxRef(txRef) | This is the unique reference, unique to the particular transaction being carried out. It is generated by the merchant for every transaction | `String` | Required
| acceptCardPayments(boolean) | Set to `true` if you want to accept payments via bank accounts, else set to `false` | `boolean` | Not Required |
| acceptGHMobileMoneyPayments(boolean) | Set to `true` if you want to accept Ghana mobile money payments, else set to `false` . For this option to work, you should set your country to `GH` and your currency to `GHS`| `boolean` | Not Required |
| onStagingEnv(boolean) | Set to `true` if you want your transactions to run in the staging environment otherwise set to `false`. Defaults to false  | `boolean` | Not Required
| allowSaveCardFeature(boolean) | Set to `true` if you want your users to be able to save their cards for later reuse otherwise set to `false` | `boolean` | Required
| setMeta(`List<Meta>`) | Pass in any other custom data you wish to pass. It takes in a `List` of `Meta` objects | List<Meta> | Not Required
| withTheme(styleId) | Sets the theme of the UI. | `int` | Not Required
| setPaymentPlan(payment_plan) | If you want to do recurrent payment, this is the payment plan ID to use for the recurring payment, you can see how to create payment plans [here](https://apeatudevelopers.readme.io/v2.0/reference#create-payment-plan) and [here](https://apeatudevelopers.readme.io/docs/recurring-billing). This is only available for card payments | `String` | Not Required
| chooseCardOrNumber() | Launch the theteller List of already saved Payment sources UI  |  N/A | Not Required
| initialize() | Launch the theteller Payment UI  |  N/A | Required when not using chooseCardOrNumber() function

> **SECURITY ALERT**
> You should never store your **API KEY** on the user's device

###  1.5 Checking for Saving Cards before initializing
You can implore the `GhMobileMoneyPresenter` and  `CardPresenter` to list already saved mobile money numbers or cards for a user to choose from by calling `checkForSavedGHMobileMoney(email)` and `checkForSavedCards(email)` on instances of them respectively. This is useful for a smooth user experience.

            GhMobileMoneyPresenter ghMobileMoneyPresenter;
            CardPresenter cardPresenter;

            ghMobileMoneyPresenter = new GhMobileMoneyPresenter(this, new GhMobileMoneyFragment());
            cardPresenter = new CardPresenter(this, new CardFragment());
            List<SavedPhone> checkForSavedMobileMoney = ghMobileMoneyPresenter.checkForSavedGHMobileMoney(email);
            List<SavedCard> checkForSavedCards = cardPresenter.checkForSavedCards(email);

            if (checkForSavedCards.isEmpty() && checkForSavedMobileMoney.isEmpty()){
                new thetellerManager(this).setAmount(Long.parseLong(amount))
                    .setEmail(email)
                    .setfName(fName)
                    .setlName(lName)
                    .setMerchant_id(merchantId)
                    .setNarration(narration)
                    .setApiUser(apiUser)
                    .setApiKey(apiKey)
                    .setTxRef(txRef)
                    .set3dUrl(dUrl)
                    .acceptCardPayments(cardSwitch.isChecked())
                    .acceptGHMobileMoneyPayments(ghMobileMoneySwitch.isChecked())
                    .onStagingEnv(!isLiveSwitch.isChecked())
                    .initialize();
            }else {
                new thetellerManager(this).setAmount(Long.parseLong(amount))
                    .setEmail(email)
                    .setfName(fName)
                    .setlName(lName)
                    .setMerchant_id(merchantId)
                    .setNarration(narration)
                    .setApiUser(apiUser)
                    .setApiKey(apiKey)
                    .setTxRef(txRef)
                    .set3dUrl(dUrl)
                    .acceptCardPayments(cardSwitch.isChecked())
                    .acceptGHMobileMoneyPayments(ghMobileMoneySwitch.isChecked())
                    .onStagingEnv(!isLiveSwitch.isChecked())
                    .chooseCardOrNumber();
            }

###  2. Handle the response
In the calling activity, override the `onActivityResult` method to receive the payment response as shown below

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.wtf("response", theteller_results);

    }

The intent's `reason` object contains the raw JSON response from the theteller API. This can be parsed to retrieve any additional payment information needed.

##  Help
* Find a bug? [Open an issue](https://github.com/apeatu/theteller-android-sdk/issues)
* Want to contribute? [Check out contributing guidelines]() and [submit a pull request](https://help.github.com/articles/creating-a-pull-request).

## Want to contribute?

Feel free to create issues and pull requests. The more concise the pull requests the better :)


## License

```
theteller's Android Dev Kit
MIT License

Copyright (c) 2017

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```