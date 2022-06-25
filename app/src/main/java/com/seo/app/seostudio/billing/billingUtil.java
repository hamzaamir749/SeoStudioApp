package com.seo.app.seostudio.billing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.seo.app.seostudio.BuildConfig;
import com.seo.app.seostudio.activties.MainActivity;
import com.seo.app.seostudio.sharedpref.savevalue;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class billingUtil {


    public static BillingClient billingClient;
    public static PurchasesUpdatedListener purchaseUpdateListener;
    public static Boolean isBillingReady = false;


    public static boolean isPremium = false;
    public static String price="";
    public static String LIFE_TIME_PRODUCT = "pro_seostudio";
    public static String LIFE_TIME_PRODUCT_DEBUG = "android.test.purchased";


    public billingUtil(final Context context) {
        isPremium = new savevalue(context).getboolean(savevalue.Companion.getIspremium());
        if (billingClient == null) {
            purchaseUpdateListener = new PurchasesUpdatedListener() {
                @Override
                public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> purchases) {
//                Toast.makeText(context, "in listened", Toast.LENGTH_SHORT).show();
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
                        for (Purchase purchase : purchases) {
                            billingUtil.this.handlePurchase(context, purchase);
                        }
                    } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {

                    } else {
                    }
                }
            };

            billingClient = BillingClient.newBuilder(context)
                    .setListener(purchaseUpdateListener)
                    .enablePendingPurchases()
                    .build();

            setupConnection(context);

        }
    }

    private void setupConnection(final Context context) {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NotNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    isBillingReady = true;
                    // Toast.makeText(context, "Setup Connection Successful", Toast.LENGTH_SHORT).show();
                    getOldPurchases(context);
                    getPrices();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
//                Toast.makeText(context, "Setup Connection Failed", Toast.LENGTH_SHORT).show();
                isBillingReady = false;
            }
        });
    }

    private void getPrices(){

        if (isBillingReady) {
            List<String> skuList = new ArrayList<>();

            skuList.add(BuildConfig.DEBUG ? LIFE_TIME_PRODUCT_DEBUG : LIFE_TIME_PRODUCT);

            SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
            params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
            billingClient.querySkuDetailsAsync(params.build(),
                    new SkuDetailsResponseListener() {
                        @Override
                        public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> skuDetailsList) {
                            // Process the result.
                            if (skuDetailsList != null) {
                                if (skuDetailsList.size() > 0) {
                                    price = skuDetailsList.get(0).getPrice();
                                }
                            }
                        }
                    });
        }
    }

    public void purchase(final Activity activity, String product) {

        if (isBillingReady) {
            List<String> skuList = new ArrayList<>();

            skuList.add(BuildConfig.DEBUG ? LIFE_TIME_PRODUCT_DEBUG : LIFE_TIME_PRODUCT);

            SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
            params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
            billingClient.querySkuDetailsAsync(params.build(),
                    new SkuDetailsResponseListener() {
                        @Override
                        public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> skuDetailsList) {
                            // Process the result.
                            if (skuDetailsList != null) {
                                if (skuDetailsList.size() > 0) {
                                    BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                                            .setSkuDetails(skuDetailsList.get(0))
                                            .build();

                                    price = skuDetailsList.get(0).getPrice();

                                    int responseCode = billingClient.launchBillingFlow(activity, billingFlowParams).getResponseCode();

                                    if (responseCode != BillingClient.BillingResponseCode.OK) {
//                                    Toast.makeText(activity, "Please try Again Later", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                    });
        } else {
//            Toast.makeText(activity, "Wait A while, then try again", Toast.LENGTH_SHORT).show();
            setupConnection(activity);
        }
    }

    private void handlePurchase(Context context, Purchase purchase) {
        AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener() {
            @Override
            public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
//            Toast.makeText(context, "Acknowledged", Toast.LENGTH_SHORT).show();
            }
        };


        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED/* && isSignatureValid(purchase)*/) {
            if (purchase.getSkus().contains(BuildConfig.DEBUG ? LIFE_TIME_PRODUCT_DEBUG : LIFE_TIME_PRODUCT)) {
                new savevalue(context).setboolean(savevalue.Companion.getIspremium(), true);
                isPremium = true;
                Toast.makeText(context, "Thanks for purchase. App restarting to get premium feature", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(context, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                System.exit(1);
                Log.i("BillingTag", "handlePurchase: premium");
            }
//            if(purchase.getSku().equals(AllSounds)) {
//                soundsUnlocked = true;
//                Log.i("BillingTag", "handlePurchase: sound");
//            }

            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
            }
        }
    }

    private void getOldPurchases(Context context) {
        Purchase.PurchasesResult result = billingClient.queryPurchases(BillingClient.SkuType.INAPP);
        List<Purchase> purchases = result.getPurchasesList();
        if (purchases != null) {
            for (Purchase purchase : purchases) {
                if (purchase.getSkus().contains(BuildConfig.DEBUG ? LIFE_TIME_PRODUCT_DEBUG : LIFE_TIME_PRODUCT)) {
                    new savevalue(context).setboolean(savevalue.Companion.getIspremium(), true);
                    isPremium = true;

                }
//                if(purchase.getSku().equals(AllSounds)){
//                    soundsUnlocked = true;
//                    Toast.makeText(context, "sounds unlocked", Toast.LENGTH_SHORT).show();
//                }
            }
        }
    }
}
