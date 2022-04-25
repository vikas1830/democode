

// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

        import com.beust.jcommander.Parameter;
        import com.google.ads.googleads.lib.GoogleAdsClient;
        import com.google.ads.googleads.v10.errors.GoogleAdsError;
        import com.google.ads.googleads.v10.errors.GoogleAdsException;
        import com.google.ads.googleads.v10.resources.AdGroup;
        import com.google.ads.googleads.v10.services.GoogleAdsRow;
        import com.google.ads.googleads.v10.services.GoogleAdsServiceClient;
        import com.google.ads.googleads.v10.services.GoogleAdsServiceClient.SearchPagedResponse;
        import com.google.ads.googleads.v10.services.SearchGoogleAdsRequest;
        import utils.ArgumentNames;
        import utils.CodeSampleParams;

        import java.io.FileNotFoundException;
        import java.io.IOException;
        import java.util.Properties;
        import javax.annotation.Nullable;

/** Gets ad groups. */
public class GetAdGroups {

    private static final int PAGE_SIZE = 1_000;

    private static class GetAdGroupsParams extends CodeSampleParams {

        @Parameter(names = ArgumentNames.CUSTOMER_ID, required = true)
        private Long customerId;

        @Parameter(names = ArgumentNames.CAMPAIGN_ID)
        private Long campaignId;
    }

    public static void main(String[] args) throws IOException {
        Properties adsProperties = new Properties();
        adsProperties.put(GoogleAdsClient.Builder.ConfigPropertyKey.CLIENT_ID.getPropertyKey(), "747244873818-p9okqngskuhlvumka7not28jilli74k3.apps.googleusercontent.com");
        adsProperties.put(GoogleAdsClient.Builder.ConfigPropertyKey.CLIENT_SECRET.getPropertyKey(),"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" );
        adsProperties.put(
                GoogleAdsClient.Builder.ConfigPropertyKey.REFRESH_TOKEN.getPropertyKey(), "1//0gO8z8QqRRp5mCgYIARAAGBASNwF-L9Irskaa3HjVNy4dSZPy0moVzQJ0fuZU7vsa9KO41Dq3YcYoSD_brU5-QaDR0qYJ928gxYE");
        adsProperties.put(
                GoogleAdsClient.Builder.ConfigPropertyKey.DEVELOPER_TOKEN.getPropertyKey(), "fwNAHt4BisaHV38Hb9DaKQ");
        GetAdGroupsParams params = new GetAdGroupsParams();
        if (!params.parseArguments(args)) {

            // Either pass the required parameters for this example on the command line, or insert them
            // into the code here. See the parameter class definition above for descriptions.
            params.customerId = Long.parseLong("7792089319");

            // Optional: Specify a campaign ID to restrict search to only a given campaign.
            params.campaignId = null;
        }

        GoogleAdsClient googleAdsClient = null;
        googleAdsClient = GoogleAdsClient.newBuilder().fromProperties(adsProperties).build();

        try {
            new GetAdGroups().runExample(googleAdsClient, params.customerId, params.campaignId);
        } catch (GoogleAdsException gae) {
            // GoogleAdsException is the base class for most exceptions thrown by an API request.
            // Instances of this exception have a message and a GoogleAdsFailure that contains a
            // collection of GoogleAdsErrors that indicate the underlying causes of the
            // GoogleAdsException.
            System.err.printf(
                    "Request ID %s failed due to GoogleAdsException. Underlying errors:%n",
                    gae.getRequestId());
            int i = 0;
            for (GoogleAdsError googleAdsError : gae.getGoogleAdsFailure().getErrorsList()) {
                System.err.printf("  Error %d: %s%n", i++, googleAdsError);
            }
            System.exit(1);
        }
    }

    /**
     * Runs the example.
     *
     * @param googleAdsClient the Google Ads API client.
     * @param customerId the client customer ID.
     * @param campaignId the campaign ID for which ad groups will be retrieved. If {@code null},
     *     returns from all campaigns.
     * @throws GoogleAdsException if an API request failed with one or more service errors.
     */
    private void runExample(
            GoogleAdsClient googleAdsClient, long customerId, @Nullable Long campaignId) {
        try (GoogleAdsServiceClient googleAdsServiceClient =
                     googleAdsClient.getLatestVersion().createGoogleAdsServiceClient()) {
            String searchQuery = "SELECT campaign.id, ad_group.id, ad_group.name FROM ad_group";
            if (campaignId != null) {
                searchQuery += String.format(" WHERE campaign.id = %d", campaignId);
            }

            // Creates a request that will retrieve all ad groups using pages of the specified page size.
            SearchGoogleAdsRequest request =
                    SearchGoogleAdsRequest.newBuilder()
                            .setCustomerId(Long.toString(customerId))
                            .setPageSize(PAGE_SIZE)
                            .setQuery(searchQuery)
                            .build();
            // Issues the search request.
            SearchPagedResponse searchPagedResponse = googleAdsServiceClient.search(request);
            // Iterates over all rows in all pages and prints the requested field values for the ad group
            // in each row.
            for (GoogleAdsRow googleAdsRow : searchPagedResponse.iterateAll()) {
                AdGroup adGroup = googleAdsRow.getAdGroup();
                System.out.printf(
                        "Ad group with ID %d and name '%s' was found in campaign with ID %d.%n",
                        adGroup.getId(), adGroup.getName(), googleAdsRow.getCampaign().getId());
            }
        }
    }
}
