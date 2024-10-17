package hello.mvc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class YouTubeVideoDownloader {
    private static final String API_KEY = "";
    private static final String CHANNEL_USERNAME = "";
    private static final String API_URL_CHANNEL = "https://www.googleapis.com/youtube/v3/channels";
    private static final String API_URL_SEARCH = "https://www.googleapis.com/youtube/v3/search";
    private static final String API_URL_VIDEO_DETAILS = "https://www.googleapis.com/youtube/v3/videos";

    public static void main(String[] args) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // 1. 채널 ID 가져오기
            String channelId = getChannelId(httpClient);
            if (channelId != null) {
                System.out.println("Channel ID: " + channelId);

                // 2. 채널의 동영상 목록 가져오기 (videoId 리스트로 저장)
                List<String> videoIds = getVideoIds(httpClient, channelId);

                // 3. videoId로 비디오 메타데이터 가져오기 (비디오 세부 정보 출력)
                for (String videoId : videoIds) {
                    getVideoDetails(httpClient, videoId);
                    String youtubeLink = "https://www.youtube.com/watch?v=" + videoId;
                    downloadVideo(youtubeLink);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getChannelId(CloseableHttpClient httpClient) throws IOException {
        String url = API_URL_CHANNEL + "?part=id&forUsername=" + CHANNEL_USERNAME + "&key=" + API_KEY;
        HttpGet request = new HttpGet(url);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            if (response.getCode() == 200) {
                String json = EntityUtils.toString(response.getEntity());
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(json);
                return rootNode.get("items").get(0).get("id").asText();
            } else {
                System.out.println("Error fetching channel ID: " + response.getCode());
                return null;
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String> getVideoIds(CloseableHttpClient httpClient, String channelId) throws IOException {
        String url = API_URL_SEARCH + "?part=snippet&channelId=" + channelId + "&maxResults=100&order=date&type=video&key=" + API_KEY;
        HttpGet request = new HttpGet(url);
        List<String> videoIds = new ArrayList<>();

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            if (response.getCode() == 200) {
                String json = EntityUtils.toString(response.getEntity());
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(json);
                for (JsonNode item : rootNode.get("items")) {
                    videoIds.add(item.get("id").get("videoId").asText());
                }
            } else {
                System.out.println("Error fetching videos: " + response.getCode());
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return videoIds;
    }

    private static void getVideoDetails(CloseableHttpClient httpClient, String videoId) throws IOException {
        String url = API_URL_VIDEO_DETAILS + "?part=snippet,statistics&id=" + videoId + "&key=" + API_KEY;
        HttpGet request = new HttpGet(url);

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            if (response.getCode() == 200) {
                String json = EntityUtils.toString(response.getEntity());
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(json);
                JsonNode videoData = rootNode.get("items").get(0);
                JsonNode snippet = videoData.get("snippet");
                JsonNode statistics = videoData.get("statistics");

                String videoTitle = snippet.get("title").asText();
                String videoDescription = snippet.get("description").asText();
                String videoPublishDate = snippet.get("publishedAt").asText();
                String likeCount = statistics.has("likeCount") ? statistics.get("likeCount").asText() : "N/A";
                String viewCount = statistics.has("viewCount") ? statistics.get("viewCount").asText() : "N/A";

                System.out.println("Title: " + videoTitle);
                System.out.println("Description: " + videoDescription);
                System.out.println("Published At: " + videoPublishDate);
                System.out.println("Like Count: " + likeCount);
                System.out.println("View Count: " + viewCount);
                System.out.println("URL: https://www.youtube.com/watch?v=" + videoId);
                System.out.println("-".repeat(40));
            } else {
                System.out.println("Error fetching video details: " + response.getCode());
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static void downloadVideo(String youtubeUrl) {
        System.out.println("youtubeUrl = " + youtubeUrl);
        try {
//            String command = "yt-dlp -f bestvideo[height<=1080]+bestaudio/best[height<=1080] " + youtubeUrl;
            String command = "yt-dlp -f bestvideo[height<=240]+bestaudio/best[height<=1080] " + youtubeUrl;
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            System.out.println("Downloaded video from: " + youtubeUrl);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}