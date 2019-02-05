package hn.service;

import hn.domain.Item;
import hn.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class BaseService {

    private static final Logger log = LoggerFactory.getLogger(BaseService.class);

    private static final int KARMA = 10000;
    private static final String GET_ITEM_URL2 = "https://hacker-news.firebaseio.com/v0/item/";
    private static final String GET_USER = "https://hacker-news.firebaseio.com/v0/user/";
    private static final String GET_500_STORIES_URL = "https://hacker-news.firebaseio.com/v0/newstories.json?print=pretty";

    private ConcurrentHashMap<String, User> karmaUsers;
    private ConcurrentHashMap<String, User> normalUsers;

    private List<Integer> newStories;

    public BaseService(){
        karmaUsers = new ConcurrentHashMap<>();
        normalUsers = new ConcurrentHashMap<>();
    }

    private void getLast500Stories() {
        log.info("getLast500Stories init");
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<Integer>> response = restTemplate.exchange(
                GET_500_STORIES_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Integer>>(){});

        this.newStories = response.getBody();
        log.info("getLast500Stories exit {}", this.newStories.size());
    }

    /**
     * service call to return the most 10 words available in the last 25 stories
     * @return
     */
    public List<Map.Entry<String, Long>> getTop10titlesLast25Stories() {
        log.info("getTop10titlesLast25Stories init");
        getLast500Stories();

        List<String> titles = getItemsTitle(this.newStories.subList(0, 25));
        List<String> words = titles.parallelStream().map(i -> i.split(" ")).flatMap(j -> Arrays.stream(j)).collect(Collectors.toList());

        log.info("getTop10titlesLast25Stories exit");
        return calculateFrequency(words);
    }

    /**
     * service call to return the most 10 words available in the last week post
     * @return
     */
    public List<Map.Entry<String, Long>> getTop10titlesLastWeek() {
        log.info("getTop10titlesLastWeek init");
        ZonedDateTime today = ZonedDateTime.now();
        ZonedDateTime lastWeek = today.minus(7, ChronoUnit.DAYS);
        long minimumEpoch = lastWeek.toEpochSecond();

        getLast500Stories();

        //cleaning last 500 stories from any possible values before last week window time.
        // and get info for the right ones
        Predicate<Item> filterOnDate = i -> i.getTime().toEpochSecond() > minimumEpoch;

        log.info("getTop10titlesLastWeek number of stories {}", this.newStories.size());
        List<Item> items = getItems(this.newStories, filterOnDate);
        List<String> itemsTitles = items.stream().map(i -> i.getTitle()).collect(Collectors.toList());


        log.info("getTop10titlesLastWeek minimumEpoch checked items {} {}", minimumEpoch, items.size());
        Item lastItem = items.get(items.size() -1 );
        items.clear();
        int min = findLastIdInThePastWeek(lastItem, minimumEpoch, 100000);

        log.info("getTop10titlesLastWeek checked min last {} {} {}", min, lastItem.getId(), lastItem.getId() - min);

        List<Integer> itemsIdToRetrieve = IntStream.range(min, lastItem.getId() -1).boxed().collect(Collectors.toList());
        List<String> missingItems = getItemsTitle(itemsIdToRetrieve);

        log.info("getTop10titlesLastWeek missing items {}", missingItems.size());
        itemsTitles.addAll(missingItems);

        List<String> words = itemsTitles.parallelStream().map(i -> i.split(" ")).flatMap(j -> Arrays.stream(j)).collect(Collectors.toList());

        log.info("getTop10titlesLast25Stories exit");
        return calculateFrequency(words);
    }


    /**
     * service call to return the most 10 words available in the last 600 stories from user with karma > 10000
     * @return
     */
    public List<Map.Entry<String, Long>> getTop10titlesFromUserWithBigKarma() {
        getLast500Stories();
        int firstId = this.newStories.get(this.newStories.size()-1);
        List<Integer> itemsIdToRetrieve = IntStream.range(firstId - 101, firstId -1).boxed().collect(Collectors.toList());

        List<Item> itemsWithKarmaUSer = getItemsTitleForKarmaUser(itemsIdToRetrieve);
        List<String> words = itemsWithKarmaUSer.parallelStream().map(i -> i.getTitle().split(" ")).flatMap(j -> Arrays.stream(j)).collect(Collectors.toList());

        log.info("getTop10titlesFromUserWithBigKarma exit");
        return calculateFrequency(words);
    }

    /**
     * it calculates the occurred frequency for every word in the list
     * @param list
     * @return
     */
    private List<Map.Entry<String, Long>> calculateFrequency(List<String> list) {
        Map<String, Long> map = list.stream()
                .collect(Collectors.groupingBy(w -> w, Collectors.counting()));

        List<Map.Entry<String, Long>> result = map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(10)
                .collect(Collectors.toList());

        return result;
    }

    /**
     * it finds recursively the next available index included within the minimum epoch time
     * @param current
     * @param minimumEpoch
     * @param step
     * @return
     */
    private int findLastIdInThePastWeek(Item current, long minimumEpoch, int step) {
        RestTemplate restTemplate = new RestTemplate();
        int nextId = current.getId() - step;
        ResponseEntity<Item> response = restTemplate.getForEntity(GET_ITEM_URL2 + nextId + ".json", Item.class);
        log.info("findLastIdInThePastWeek {}",current.toShortString());
        Item next = response.getBody();
        if(next != null) {
            if(step == 1){
                return current.getId() + 1;
            }
            if (next.getTime().toEpochSecond() < minimumEpoch) {
                log.info("findLastIdInThePastWeek < next {} ", (int)Math.ceil(step/2));
                return findLastIdInThePastWeek(current, minimumEpoch, (int)Math.ceil(step/2));
            } else if(next.getTime().toEpochSecond() == minimumEpoch) {

                log.info("findLastIdInThePastWeek = next {} ", nextId);
                return nextId;
            } else {
                log.info("findLastIdInThePastWeek > next {} ", step+step);
                return findLastIdInThePastWeek(next, minimumEpoch, step+step);
            }
        } else {
            log.info("findLastIdInThePastWeek  next NULL ");
        }

        return -1;
    }

    private List<Item> getItems(List<Integer> ids, Predicate<Item> filter){

        List<CompletableFuture<Item>> futures =
                ids.stream()
                        .map(id -> getItemAsync(id))
                        .collect(Collectors.toList());

        List<Item> result =
                futures.stream()
                        .map(CompletableFuture::join)
                        .filter(filter)
                        .collect(Collectors.toList());

        log.info("getItems joined");
        return result;
    }

    private List<Item> getItemsTitleForKarmaUser(List<Integer> ids) {

        List<CompletableFuture<Item>> futures =
                ids.stream()
                        .map(id -> getItemTitleWithUserKarmaAsync(id))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

        List<Item> result =
                futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList());

        log.info("getItemsTitleForKarmaUser joined");
        return result;
    }


    private List<String> getItemsTitle(List<Integer> ids){

        List<CompletableFuture<String>> futures =
                ids.stream()
                        .map(id -> getItemTitleAsync(id))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

        List<String> result =
                futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList());

        log.info("getItemsTitle joined");
        return result;
    }

    @Async
    CompletableFuture<Item> getItemAsync(Integer id){

        RestTemplate restTemplate = new RestTemplate();
        CompletableFuture<Item> future = CompletableFuture.supplyAsync(new Supplier<Item>() {
            @Override
            public Item get() {
                final String _uri = GET_ITEM_URL2 + id + ".json";
                final Item item = restTemplate.getForObject(_uri, Item.class);
                return item;
            }
        });

        return future;
    }

    @Async
    CompletableFuture<Item> getItemTitleWithUserKarmaAsync(Integer id){

        RestTemplate restTemplate = new RestTemplate();
        CompletableFuture<Item> future = CompletableFuture.supplyAsync(new Supplier<Item>() {
            @Override
            public Item get() {
                final String _uri = GET_ITEM_URL2 + id + ".json";
                final Item item = restTemplate.getForObject(_uri, Item.class);
                return item;
            }
        }).thenApplyAsync(i -> {

            String userId = i.getBy();
            if(userId == null)
                return null;

            log.info("current user {}", userId);
            if(karmaUsers.containsKey(userId)) {
                return i;
            }
            if(normalUsers.containsKey(userId)) {
                return null;
            }

            final String _uri = GET_USER + userId + ".json";
            final User newUser = restTemplate.getForObject(_uri, User.class);

            if(newUser == null)
                return null;

            if(newUser.getKarma() >= KARMA){
                karmaUsers.put(userId, newUser);
                return i;
            }
            normalUsers.put(userId, newUser);

            return null;
        });

        return future;
    }

    @Async
    CompletableFuture<String> getItemTitleAsync(Integer id){

        RestTemplate restTemplate = new RestTemplate();
        CompletableFuture<String> future = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                final String _uri = GET_ITEM_URL2 + id + ".json";
                final Item item = restTemplate.getForObject(_uri, Item.class);
                return (item != null)? item.getTitle() : null;
            }
        });

        return future;
    }


    @Async
    CompletableFuture<Integer> getUserAsync(String id){

        RestTemplate restTemplate = new RestTemplate();
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                final String _uri = GET_USER + id + ".json";
                final User user = restTemplate.getForObject(_uri, User.class);
                return (user != null)? user.getKarma() : null;
            }
        });

        return future;
    }
}