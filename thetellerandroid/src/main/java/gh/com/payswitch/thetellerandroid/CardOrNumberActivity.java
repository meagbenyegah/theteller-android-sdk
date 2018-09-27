package gh.com.payswitch.thetellerandroid;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.parceler.Parcels;

import java.util.List;

import gh.com.payswitch.thetellerandroid.data.Callbacks;
import gh.com.payswitch.thetellerandroid.data.SavedCard;
import gh.com.payswitch.thetellerandroid.data.SavedPhone;
import gh.com.payswitch.thetellerandroid.data.SharedPrefsRequestImpl;
import gh.com.payswitch.thetellerandroid.ghmobilemoney.SavedPhoneVP;

import static gh.com.payswitch.thetellerandroid.thetellerConstants.theteller;
import static gh.com.payswitch.thetellerandroid.thetellerConstants.theteller_PARAMS;

public class CardOrNumberActivity extends FragmentActivity {

    SavedPhoneVP ghMobileMoneyPresenter;
    thetellerInitializer thetellerInitializer;
    thetellerManager thetellerManager;
    int theme;
    Button differentNumberButton;
    public static String BASE_URL;
    boolean withCard = true;
    boolean withGHMobileMoney = true;
    boolean isLive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            thetellerInitializer = Parcels.unwrap(getIntent().getParcelableExtra(theteller_PARAMS));
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.d(theteller, "Error retrieving initializer");
        }

        theme = thetellerInitializer.getTheme();

        if (theme != 0) {
            try {
                setTheme(theme);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (thetellerInitializer.isStaging()) {
            BASE_URL = thetellerConstants.STAGING_URL;
        }
        else {
            BASE_URL = thetellerConstants.LIVE_URL;
        }

        setContentView(R.layout.activity_card_or_number);
        differentNumberButton = (Button) findViewById(R.id.different_number_btn);
        thetellerManager = new thetellerManager(this);

        if (thetellerInitializer != null) {


            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.theteller_recycler3);

            SharedPrefsRequestImpl sharedMgr = new SharedPrefsRequestImpl(this);
            List<SavedCard> cardList = sharedMgr.getSavedCards(thetellerInitializer.getEmail());
            List<SavedPhone> phoneList = sharedMgr.getSavedGHMobileMoney(thetellerInitializer.getEmail());

            SavedCNRecyclerAdapter cnAdapter =new SavedCNRecyclerAdapter();
            cnAdapter.setCard(cardList);
            cnAdapter.setPhone(phoneList);
            ghMobileMoneyPresenter = new SavedPhoneVP();

            cnAdapter.setSavedCardSelectedListener(new Callbacks.SavedCardSelectedListener() {
                @Override
                public void onCardSelected(SavedCard savedCard) {
                    CVVFragment CVVFragment = new CVVFragment();
                    CVVFragment.setthetellerVariable(thetellerInitializer, savedCard);
                    CVVFragment.show(getSupportFragmentManager(), "enter_cvv");
                }
            });
            cnAdapter.setSavedPhoneSelectedListener(new Callbacks.SavedPhoneSelectedListener() {
                @Override
                public void onPhoneSelected(SavedPhone savedPhone) {
                    final PayloadBuilder builder = new PayloadBuilder();
                    builder.setAmount(thetellerInitializer.getAmount() + "")
                            .setCurrency(thetellerInitializer.getCurrency())
                            .setEmail(thetellerInitializer.getEmail())
                            .setFirstname(thetellerInitializer.getfName())
                            .setLastname(thetellerInitializer.getlName())
                            .setIP(Utils.getDeviceImei(CardOrNumberActivity.this))
                            .setTxRef(thetellerInitializer.getTxRef())
                            .setMeta(thetellerInitializer.getMeta())
                            .setPBFPubKey(thetellerInitializer.getApiKey())
                            .setDevice_fingerprint(Utils.getDeviceImei(CardOrNumberActivity.this));

                    if (thetellerInitializer.getPayment_plan() != null) {
                        builder.setPaymentPlan(thetellerInitializer.getPayment_plan());
                    }
                    builder.setPhonenumber(savedPhone.getPhoneNumber());
                    builder.setNetwork(savedPhone.getNetwork());
                    final Payload body = builder.createGhMobileMoneyPayload();
                    ghMobileMoneyPresenter.chargeGhMobileMoney(body, thetellerConstants.API_KEY, CardOrNumberActivity.this);
                    Log.wtf("phone number on click", body.getPhonenumber());
                }
            });


            differentNumberButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pushToMainPage();
                }
            });
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(cnAdapter);
//            setUpRecyclerView(recyclerView, cnAdapter);
        }else {
            Log.d("empty init","the initializer is still empty");
        }
    }

    private void pushToMainPage() {
        String email;
        Double amount;
        String publicKey;
        String txRef;
        String narration;
        String currency;
        String fName;
        String lName;

        email = thetellerInitializer.getEmail();
        amount = thetellerInitializer.getAmount();
        publicKey = thetellerInitializer.getApiKey();
        txRef = thetellerInitializer.getTxRef();
        narration = thetellerInitializer.getNarration();
        currency = thetellerInitializer.getCurrency();
        fName = thetellerInitializer.fName;
        lName = thetellerInitializer.lName;

        new thetellerManager(CardOrNumberActivity.this).setAmount(amount)
            .setCurrency(currency)
            .setEmail(email)
            .setfName(fName)
            .setlName(lName)
            .setNarration(narration)
            .setApiKey(publicKey)
            .setTxRef(txRef)
            .acceptCardPayments(withCard)
            .acceptGHMobileMoneyPayments(withGHMobileMoney)
            .onStagingEnv(!isLive)
//                    .setMeta(meta)
//                    .withTheme(R.style.TestNewTheme)
            .initialize();
    }

}