package net.atayun.bazooka.combase.dcos.api;

import feign.Feign;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Response;
import feign.codec.ErrorDecoder;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import mesosphere.client.common.ModelUtils;
import mesosphere.dcos.client.DCOSAPIInterceptor;
import mesosphere.dcos.client.DCOSException;
import mesosphere.dcos.client.model.DCOSAuthCredentials;
import mesosphere.dcos.client.model.DCOSAuthToken;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author WangSongJun
 * @date 2019-09-18
 */
public class DcosApiClient {
    public static DcosApi getInstance(String endpoint) {
        return buildInstance(endpoint, b -> {
        });
    }

    public static DcosApi getInstance(String endpoint, final DCOSAuthCredentials authCredentials) {
        // Need to use a non-authenticated DCOSClient instance to perform the authorization and token refresh to avoid
        // the complexity of synchronizing around checking whether a token needs to be refreshed.
        return buildInstance(endpoint, b ->
                b.requestInterceptor(new DcosApiAuthTokenHeaderInterceptor(authCredentials, getInstance(endpoint))));
    }

    private static DcosApi buildInstance(String endpoint, Consumer<Feign.Builder> customize) {
        GsonDecoder decoder = new GsonDecoder(ModelUtils.GSON);
        GsonEncoder encoder = new GsonEncoder(ModelUtils.GSON);

        Feign.Builder builder = Feign.builder()
                .encoder(encoder)
                .decoder(decoder)
                .errorDecoder(new DcosApiErrorDecoder());
        customize.accept(builder);
        builder.requestInterceptor(new DCOSAPIInterceptor());

        return builder.target(DcosApi.class, endpoint);
    }

    private static class DcosApiErrorDecoder implements ErrorDecoder {
        @Override
        public Exception decode(String methodKey, Response response) {

            String details;
            try {
                details = IOUtils.toString(response.body().asInputStream(), "UTF-8");
            } catch (NullPointerException | IOException e) {
                details = "Unable to read response body";
            }
            return new DCOSException(response.status(), response.reason(), methodKey, details);
        }
    }


    static class DcosApiAuthTokenHeaderInterceptor implements RequestInterceptor {
        private final DcosApi dcosClient;
        private final DCOSAuthCredentials authCredentials;
        private DCOSAuthToken dcosAuthToken;

        DcosApiAuthTokenHeaderInterceptor(DCOSAuthCredentials authCredentials, DcosApi dcosClient) {
            Objects.requireNonNull(authCredentials, "authCredentials:null");
            Objects.requireNonNull(dcosClient, "dcosClient:null");
            this.dcosClient = dcosClient;
            this.authCredentials = authCredentials;
        }

        @Override
        public void apply(RequestTemplate template) {
            if (this.dcosAuthToken == null || this.dcosAuthToken.requiresRefresh()) {
                this.dcosAuthToken = this.dcosClient.authenticate(this.authCredentials).toDCOSAuthToken();
            }

            template.header("Authorization", new String[]{"token=" + this.dcosAuthToken.getToken()});
        }
    }
}
