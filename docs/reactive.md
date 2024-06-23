## Reactive (WebFlux)

서블릿 기반은 클라이언트의 요청을 미리 만들어둔 쓰레드 풀에 블록 후 요청이 완료되면 해제한다<br>
입력 및 출력을 수행하는 동안 해당 쓰레드는 블로킹이 된다 (블로킹 I/O) -> 수많은 요청에 불리하다<br>

반면에 리액티브 기반은 논블로킹 방식으로 동시성을 요구하는 애플리케이션에서 성능을 발휘한다<br>
쓰레드를 cpu코어 수와 매칭시켜 컨텍스트 스위칭 비용이 발생하지 않도록 한다<br>
클라이언트의 요청은 비동기적으로 처리된다. 이때 블록은 되지 않는다 (논 블로킹 I/O)<br>
요청이 완료되면 콜백이나 Promise를 응답한다<br>
I/O 바운드가 많은 작업에 최적화 되어 있다<br>

![img_2.png](img_2.png)

또한 함수형 스타일로 개발하게되어 동시처리에 장점을 가진다
```java
import java.util.Arrays;
import java.util.List;

public class FunctionalExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        int sum = numbers.stream()
                         .filter(number -> number % 2 == 0)     
                         .map(number -> number * number)        
                         .reduce(0, Integer::sum);              // 합계

        System.out.println("Sum of squares of even numbers: " + sum);
    }
}
```
I/O 바운드 작업을 최적화 하기 위해서는 비동기 I/O를 이용한 비동기 처리, 캐싱, 병렬처리등의 방법이 있다<br>
### 비동기 I/O

Java에서 비동기 I/O는 `CompletableFuture`나 `java.nio` 패키지를 사용하여 구현할 수 있다<br>
```javaimport java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class AsyncNetworkRequestExample {
    public static void main(String[] args) {
        CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL("https://api.example.com/data");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }

                in.close();
                conn.disconnect();
                return content.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }).thenAccept(response -> System.out.println("Response: " + response));
    }
}
```
`runAsync`는 반환 값이 없는 Runnable 작업을 비동기로 실행한다<br>
```java
CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
    // 비동기 작업 수행
});
```

`supplyAsync`는 값을 반환하는 Supplier 작업을 비동기로 실행한다<br>
```java
CompletableFuture<T> future = CompletableFuture.supplyAsync(() -> {
    // 비동기 작업 수행 후 값 반환
    return someValue;
});
```


### Java NIO
`AsynchronousFileChannel`를 이용해서 비동기 I/O 작업을 수행하는 채널을 사용한다<br>
비동기 읽기
```java
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.io.IOException;

public class AsyncFileReadExample {
    public static void main(String[] args) {
        Path filePath = Paths.get("example.txt");
        
        // 비동기 파일 채널을 열고 읽기 작업을 시작합니다.
        try (AsynchronousFileChannel asyncFileChannel = AsynchronousFileChannel.open(filePath, StandardOpenOption.READ)) {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            Future<Integer> future = asyncFileChannel.read(buffer, 0);
            
            // 다른 작업을 수행할 수 있습니다.
            System.out.println("Reading file asynchronously...");

            // 비동기 작업이 완료될 때까지 기다립니다.
            Integer bytesRead = future.get();
            System.out.println("Bytes read: " + bytesRead);

            buffer.flip();
            while (buffer.hasRemaining()) {
                System.out.print((char) buffer.get());
            }
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
```
비동기 쓰기
```java
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.io.IOException;

public class AsyncFileWriteExample {
    public static void main(String[] args) {
        Path filePath = Paths.get("example.txt");
        String content = "Hello, Async World!";
        ByteBuffer buffer = ByteBuffer.wrap(content.getBytes());

        // 비동기 파일 채널을 열고 쓰기 작업을 시작합니다.
        try (AsynchronousFileChannel asyncFileChannel = AsynchronousFileChannel.open(filePath, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
            Future<Integer> future = asyncFileChannel.write(buffer, 0);

            // 다른 작업을 수행할 수 있습니다.
            System.out.println("Writing to file asynchronously...");

            // 비동기 작업이 완료될 때까지 기다립니다.
            future.get();
            System.out.println("Write operation completed.");
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
```
`Future` - 비동기 작업의 결과<br>
`flip()` - ByteBuffer에서 읽기 작업을 시작하기 전에 호출, 버퍼의 현재 위치를 한계로 설정하고 위치를 0으로 설정<br>


[Back to main README](../README.md)