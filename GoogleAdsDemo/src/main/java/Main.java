import com.google.ads.googleads.lib.GoogleAdsClient;
import com.google.ads.googleads.v10.services.GoogleAdsRow;
import com.google.ads.googleads.v10.services.GoogleAdsServiceClient;
import com.google.ads.googleads.v10.services.SearchGoogleAdsStreamRequest;
import com.google.ads.googleads.v10.services.SearchGoogleAdsStreamResponse;
import com.google.api.gax.rpc.ServerStream;
import com.google.auth.Credentials;
import com.google.auth.oauth2.OAuth2Credentials;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws IOException {

        Properties adsProperties = new Properties();
        adsProperties.put(GoogleAdsClient.Builder.ConfigPropertyKey.CLIENT_ID.getPropertyKey(), "747244873818-p9okqngskuhlvumka7not28jilli74k3.apps.googleusercontent.com");
        adsProperties.put(GoogleAdsClient.Builder.ConfigPropertyKey.CLIENT_SECRET.getPropertyKey(),"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" );
        adsProperties.put(
                GoogleAdsClient.Builder.ConfigPropertyKey.REFRESH_TOKEN.getPropertyKey(), "1//0gO8z8QqRRp5mCgYIARAAGBASNwF-L9Irskaa3HjVNy4dSZPy0moVzQJ0fuZU7vsa9KO41Dq3YcYoSD_brU5-QaDR0qYJ928gxYE");
        adsProperties.put(
                GoogleAdsClient.Builder.ConfigPropertyKey.DEVELOPER_TOKEN.getPropertyKey(), "fwNAHt4BisaHV38Hb9DaKQ");
        GoogleAdsClient googleAdsClient = GoogleAdsClient.newBuilder()
                .fromProperties(adsProperties)
                .build();
        runExample(googleAdsClient, Long.parseLong("7792089319"));
    }
    //give campaigns
    public static void runExample(GoogleAdsClient googleAdsClient, long customerId) {
        try (GoogleAdsServiceClient googleAdsServiceClient =
                     googleAdsClient.getLatestVersion().createGoogleAdsServiceClient()) {
            String query = "SELECT campaign.id, campaign.name FROM campaign ORDER BY campaign.id";
            // Constructs the SearchGoogleAdsStreamRequest.
            SearchGoogleAdsStreamRequest request =
                    SearchGoogleAdsStreamRequest.newBuilder()
                            .setCustomerId(Long.toString(customerId))
                            .setQuery(query)
                            .build();

            // Creates and issues a search Google Ads stream request that will retrieve all campaigns.
            ServerStream<SearchGoogleAdsStreamResponse> stream =
                    googleAdsServiceClient.searchStreamCallable().call(request);

            // Iterates through and prints all of the results in the stream response.
            for (SearchGoogleAdsStreamResponse response : stream) {
                for (GoogleAdsRow googleAdsRow : response.getResultsList()) {
                    System.out.printf(
                            "Campaign with ID %d and name '%s' was found.%n",
                            googleAdsRow.getCampaign().getId(), googleAdsRow.getCampaign().getName());
                }
            }
        }
    }
}
