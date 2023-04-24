package com.example.demo;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.bo.ServiceDTO;
import com.example.demo.util.CookieArray;
import com.example.demo.util.UserAgentArray;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import com.google.gson.Gson;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest(classes = DemoApplicationTests.class)
@Slf4j
class DemoApplicationTests {

    @Test
    void contextLoads() {
    }


    // 本地启动多个测试类(单线程),获取keyWords

    @Test
    public void vnAndTh01() {
        ArrayList<String> res = FileUtil.readLines(new File("/Users/malingkai/Downloads/movie_community_1672111214.txt"), CharsetUtil.UTF_8, new ArrayList<>());
        List<String> subList = res.subList(0, 325);
        ArrayList<String> list = getKeywords(subList);
        FileUtil.writeLines(list, new File("/Users/malingkai/Downloads/movie_00.txt"), "UTF-8", true);
    }

    @Test
    public void vnAndTh02() {
        ArrayList<String> res = FileUtil.readLines(new File("/Users/malingkai/Downloads/movie_community_1672111214.txt"), CharsetUtil.UTF_8, new ArrayList<>());
        List<String> subList = res.subList(2500, 5000);
        ArrayList<String> list = getKeywords(subList);
        FileUtil.writeLines(list, new File("/Users/malingkai/Downloads/movie_02.txt"), "UTF-8", true);
    }

    @Test
    public void vnAndTh03() {
        ArrayList<String> res = FileUtil.readLines(new File("/Users/malingkai/Downloads/movie_community_1672111214.txt"), CharsetUtil.UTF_8, new ArrayList<>());
        List<String> subList = res.subList(5000, 7500);
        ArrayList<String> list = getKeywords(subList);
        FileUtil.writeLines(list, new File("/Users/malingkai/Downloads/movie_03.txt"), "UTF-8", true);
    }

    @Test
    public void vnAndTh04() {
        ArrayList<String> res = FileUtil.readLines(new File("/Users/malingkai/Downloads/movie_community_1672111214.txt"), CharsetUtil.UTF_8, new ArrayList<>());
        List<String> subList = res.subList(7500, res.size());
        ArrayList<String> list = getKeywords(subList);
        FileUtil.writeLines(list, new File("/Users/malingkai/Downloads/movie_04.txt"), "UTF-8", true);
    }

    private ArrayList<String> getKeywords(List<String> res) {
        ArrayList<String> list = new ArrayList<>();
        for (int k = 0; k < res.size(); k++) {
            JSONObject mapTypes = JSON.parseObject(res.get(k));
            try {
                JSONArray names = mapTypes.getJSONArray("name");
                System.out.println(k + "/" + res.size() + "------" + names);

                JSONArray result = new JSONArray();
                String lang = mapTypes.get("lang").toString();
                for (Object o : names) {
                    String q = o.toString();
                    String url = "https://www.google.com/complete/search";//指定URL
                    Map<String, Object> params = setRequestParam(lang, q);
                    Map<String, String> headers = setRequestHeader(lang);

                    //发送get请求并接收响应数据
                    String response = HttpUtil.createGet(url).addHeaders(headers).form(params).execute().body();
                    String validResponse = response.substring(4);
                    JSONArray json = JSONArray.parseArray(validResponse);

                    JSONArray keyWords = json.getJSONArray(0);
                    handleKeywords(result, keyWords);
                }
                mapTypes.put("keywords", result);
            } catch (Exception e) {
                log.error("第{}组数据 error:{}", k + 1, e.getMessage());
                mapTypes.put("keywords", new JSONArray());
            }
            list.add(mapTypes.toString());
        }
        return list;
    }

    private void handleKeywords(JSONArray result, JSONArray keyWords) {
        for (int i = 0; i < keyWords.size(); i++) {
            JSONArray subArray = keyWords.getJSONArray(i);
            String key = subArray.get(0).toString();
            if (key.contains("<b>")) {
                key = key.replace("<b>", "");
            }
            if (key.contains("</b>")) {
                key = key.replace("</b>", "");
            }
            result.add(key);
        }
    }

    private Map<String, Object> setRequestParam(String lang, String q) {
        Map<String, Object> map = new HashMap<>();//存放参数
        map.put("q", q);
        map.put("cp", 19);
        map.put("client", "gws-wiz-serp");
        map.put("xssi", "t");
        map.put("hl", lang);
        map.put("authuser", "0");
        map.put("pq", "32");
        map.put("psi", "OVx8Y5jrKtKz2roP_MCb4AQ.1669094458724");
        map.put("dpr", "2");
        return map;
    }

    private Map<String, String> setRequestHeader(String lang) {
        Map<String, String> headers = new HashMap<>();//存放请求头，可以存放多个请求头
        int randomCookie = new Random().nextInt(CookieArray.COOKIE.length);
        headers.put("cookie", CookieArray.COOKIE[randomCookie]);
        headers.put("accept-encoding", "deflate");
        headers.put("accept-language", lang);
        int randomUserAgent = new Random().nextInt(UserAgentArray.USER_AGENT.length);
        headers.put("User-Agent", UserAgentArray.USER_AGENT[randomUserAgent]);
        return headers;
    }


    @Test
    public void analyzeServiceLogTime() {

        try {
            InputStream inputStream = new BufferedInputStream(new FileInputStream("/Users/malingkai/Downloads/serviceTime.log.2022-10-17.txt"));
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
//            Map<String, Integer> map = new HashMap<>();
//            Map<String, Integer> countMap = new HashMap<>();
            String str = null;
            List<ServiceDTO> list = new ArrayList<>();
            while ((str = br.readLine()) != null) {
                String[] strings = str.split("INFO");
                String[] split = strings[1].split("=");

                ServiceDTO dto = new ServiceDTO();
                dto.setKey(split[0]);
                dto.setValue(Integer.valueOf(split[1].substring(0, split[1].length() - 2)));
                list.add(dto);
//                if (!map.containsKey(split[0])) {
//                    map.put(split[0], Integer.valueOf(split[1].substring(0,split[1].length()-2)));
//                }else {
//                    Integer count = countMap.get(split[0]);
//                    countMap.put(split[0], count == null ? 1 : ++count);
//                    Integer times = (map.get(split[0])+Integer.valueOf(split[1].substring(0,split[1].length()-2)));
//                    map.put(split[0], times);
//                }
            }
//            for (String s : map.keySet()) {
//                map.put(s, map.get(s) / countMap.get(s));
//            }
//            // result平均值
//            Map<String, Integer> result = map.entrySet().stream()
//                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
//                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
//                            (oldValue, newValue) -> oldValue, LinkedHashMap::new));
//            System.out.println(result);
            //FileUtil.writeUtf8Map(result,new File("/Users/malingkai/Downloads/result0922.txt"),"=",true);


            // 对list按key进行分组
            Map<String, String> percentMap = new HashMap<>();
            Map<String, Integer> medianMap = new HashMap<>();
            Map<String, Integer> maxMap = new HashMap<>();
            Map<String, Integer> minMap = new HashMap<>();
            Map<String, Double> aveMap = new HashMap<>();

            DecimalFormat df = new DecimalFormat("0.00%");
            Map<String, List<ServiceDTO>> baseMap = list.stream().collect(Collectors.groupingBy(ServiceDTO::getKey));
            for (String s : baseMap.keySet()) {
                List<ServiceDTO> dtos = baseMap.get(s);
                //统计每组的中位数
                dtos.sort(Comparator.comparing(ServiceDTO::getValue));
                medianMap.put(s, dtos.get(dtos.size() / 2).getValue());

                // 每组的最大值和最小值
                Integer maxValue = dtos.stream().max(Comparator.comparing(ServiceDTO::getValue)).get().getValue();
                maxMap.put(s, maxValue);
                Integer minValue = dtos.stream().min(Comparator.comparing(ServiceDTO::getValue)).get().getValue();
                minMap.put(s, minValue);
                // 每组的value求和
                //int sum = dtos.stream().mapToInt(ServiceDTO::getValue).sum();
                // 每组的value平均值
                //int average = sum / dtos.size();

                // 每组的value平均值
                double average = dtos.stream().mapToInt(ServiceDTO::getValue).average().getAsDouble();
                aveMap.put(s, average);
                // 统计每组大于平均值所占总数的比例
                List<ServiceDTO> collect = dtos.stream().filter(d -> d.getValue() > aveMap.get(s)).collect(Collectors.toList());
                percentMap.put(s, df.format((float) collect.size() / dtos.size()));
            }
            FileUtil.writeUtf8Map(percentMap, new File("/Users/malingkai/Downloads/resultPercent20221017.txt"), "=", true);
            FileUtil.writeUtf8Map(medianMap, new File("/Users/malingkai/Downloads/resultMedian20221017.txt"), "=", true);
            FileUtil.writeUtf8Map(maxMap, new File("/Users/malingkai/Downloads/resultMax20221017.txt"), "=", true);
            FileUtil.writeUtf8Map(minMap, new File("/Users/malingkai/Downloads/resultMin20221017.txt"), "=", true);
            FileUtil.writeUtf8Map(aveMap, new File("/Users/malingkai/Downloads/resultAverage20221017.txt"), "=", true);
//            // resMaxMap最大值
//            Map<String, ServiceDTO> res1 = list.parallelStream().collect(Collectors.toMap(ServiceDTO::getKey, Function.identity(), (c1, c2) -> c1.getValue() > c2.getValue() ? c1 : c2));
//            Map<String, Integer> maxMap = new HashMap<>();
//            for (String s : res1.keySet()) {
//                maxMap.put(s, res1.get(s).getValue());
//            }
//            Map<String, Integer> resMaxMap = new LinkedHashMap<>();
//            maxMap.entrySet().stream()
//                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
//                    .forEachOrdered(x -> resMaxMap.put(x.getKey(), x.getValue()));
//            //FileUtil.writeUtf8Map(resMaxMap,new File("/Users/malingkai/Downloads/resultMax0922.txt"),"=",true);
//
//            // resMinMap最小值
//            Map<String, ServiceDTO> res2 = list.parallelStream().collect(Collectors.toMap(ServiceDTO::getKey, Function.identity(), (c1, c2) -> c1.getValue() < c2.getValue() ? c1 : c2));
//            Map<String, Integer> minMap = new HashMap<>();
//            for (String s : res2.keySet()) {
//                minMap.put(s, res2.get(s).getValue());
//            }
//            Map<String, Integer> resMinMap = new LinkedHashMap<>();
//            minMap.entrySet().stream()
//                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
//                    .forEachOrdered(x -> resMinMap.put(x.getKey(), x.getValue()));
//            //FileUtil.writeUtf8Map(resMinMap,new File("/Users/malingkai/Downloads/resultMin0922.txt"),"=",true);
//

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //    private ArrayList<String> getKeywords(List<String> res,String cookie) {
//        ArrayList<String> list = new ArrayList<>();
//        for (int k = 0; k < res.size(); k++) {
//
//            Map mapTypes = JSON.parseObject(res.get(k));
//            LinkedHashMap<String, Object> resMap = new LinkedHashMap<>();
//            resMap.put("id", mapTypes.get("id"));
//            resMap.put("name", mapTypes.get("name"));
//            resMap.put("categories", mapTypes.get("categories"));
//            resMap.put("lang", mapTypes.get("lang"));
//
//            try {
//                for (Object obj : mapTypes.keySet()) {
//                    if ("name".equals(obj.toString())) {
//                        Object names = mapTypes.get(obj);
//
//                        JSONArray array = JSON.parseArray(names.toString());
//                        System.out.println(k + "------" + array);
//                        JSONArray result = new JSONArray();
//                        String lang = mapTypes.get("lang").toString();
//                        for (Object o : array) {
//                            String q = o.toString();
//                            String url = "https://www.google.com/complete/search";//指定URL
//                            Map<String, Object> map = setRequestParam(lang, q);
//                            HashMap<String, String> headers = setRequestHeader(lang,cookie);
//
//                            //发送get请求并接收响应数据
//                            String response = HttpUtil.createGet(url).addHeaders(headers).form(map).execute().body();
//                            String resString = response.substring(4);
//                            JSONArray json = JSONArray.parseArray(resString);
//
//                            JSONArray keyWords = json.getJSONArray(0);
//                            for (int i = 0; i < keyWords.size(); i++) {
//                                JSONArray subArray = keyWords.getJSONArray(i);
//                                String key = subArray.get(0).toString();
//                                if (key.contains("<b>")) {
//                                    key = key.replace("<b>", "");
//                                }
//                                if (key.contains("</b>")) {
//                                    key = key.replace("</b>", "");
//                                }
//                                result.add(key);
//                            }
//                        }
//                        resMap.put("keywords", result);
//                    }
//                }
//            } catch (Exception e) {
//                log.error("第{}组数据 error:{}", k + 1, e.getMessage());
//                resMap.put("keywords", new JSONArray());
//            }
//            String dto = JSON.toJSONString(resMap);
//            list.add(dto);
//        }
//        return list;
//    }
    public static int index, length;

    @Test
    public void longestPalindrome1() {
        String s = "abddba";
        if (s.length() < 2) {
            System.out.println(s);
        }
        for (int i = 0; i < s.length(); i++) {
            Palindrome1(s, i, i);
            Palindrome1(s, i, i + 1);
        }
        System.out.println(s.substring(index, index + length));
    }

    public void Palindrome1(String s, int l, int r) {
        while (l >= 0 && r < s.length() && s.charAt(l) == s.charAt(r)) {
            l--;
            r++;
        }
        if (length < r - l - 1) {
            index = l + 1;
            length = r - l - 1;
        }
    }

    @Test
    public void stringTransInt() {
        String s = "-9ll5211";
        if (s.length() == 0) {
            System.out.println(0);
        }
        char[] array = s.toCharArray();
        int flag = 0;
        if (array[0] == '+') {
            flag = 1;
        } else if (array[0] == '-') {
            flag = 2;
        }
        int start = flag > 0 ? 1 : 0;
        int res = 0;
        for (int i = start; i < array.length; i++) {
            if (!Character.isDigit(array[i])) {
                System.out.println(0);
                break;
            }
            int temp = array[i] - '0';
            res = res * 10 + temp;
        }
        System.out.println(flag > 1 ? -res : res);
    }

    @Test
    public void asd(int[] array) {
        int oddCount = 0, oddBegin = 0;
        for (int i = 0; i < array.length; i++) {
            if ((array[i] & 1) == 1) oddCount++;
        }
        int[] res = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            if ((array[i] & 1) == 1) {
                res[oddBegin++] = array[i];
            } else {
                res[oddCount++] = array[i];
            }
        }
        for (int i = 0; i < res.length; i++) {
            array[i] = res[i];
        }
    }

    @Test
    public void judgePopStackOrder(int[] pushEle, int[] popEle) {
        Stack<Integer> stack = new Stack<>();
        int popIndex = 0;
        for (int i = 0; i < pushEle.length; i++) {
            stack.push(pushEle[i]);
            while (!stack.isEmpty() && popEle[popIndex] == stack.peek()) {
                stack.pop();
                popIndex++;
            }
        }
        System.out.println(stack.isEmpty());
    }

    /*
     * public void judgeIsPopOrder(int[] pushEle,int[] popEle){
     *       Stack stack = new Stack<>();
     *       借助辅助栈
     *       for(int i = 0;i<pushEle.length;i++){
     *           int ele = pushEle[i];
     *           stack.push(ele);
     *           while(!stack.isEmpty() && popEle[popIndex++] == stack.peek()){
     *               stack.pop();
     *               popIndex++;
     *           }
     *       }
     *
     * }
     *
     * */



    @Test
    public void shellSort() {
        int[] arr = {1, 2, 99, 34, 23, 8};
        int gap = arr.length / 2;
        while (gap > 0) {
            for (int i = gap; i < arr.length; i++) {
                int curr = arr[i];
                int n = i - gap;
                while (n >= 0 && arr[n] > curr) {
                    arr[n + gap] = arr[n];
                    n -= gap;
                }
                arr[n + gap] = curr;
            }
            gap = gap / 2;
        }
        System.out.println(JSON.toJSONString(arr));
    }


    @Test
    // 给你一个以字符串表示的非负整数 num 和一个整数 k ，移除这个数中的 k 位数字，使得剩下的数字最小。
    // 请你以字符串形式返回这个最小的数字。
    public void removeKNumber(String s, int k) {
        Deque<Integer> stack = new LinkedList<>();
        for (int i = 0; i < s.length(); i++) {
            int num = s.charAt(i) - '0';
            while (!stack.isEmpty() && stack.peekFirst() > num && k > 0) {
                stack.pollFirst();
                k--;
            }
            stack.offerFirst(num);
        }
        while (!stack.isEmpty() && k > 0) {
            stack.pollFirst();
            k--;
        }
        StringBuilder sb = new StringBuilder();
        while (!stack.isEmpty()) {
            sb.append(stack.pollLast());
        }
        String res = sb.toString();
        int inx = 0;
        for (int i = 0; i < res.length(); i++) {
            if (res.charAt(i) == '0') {
                inx++;
            } else {
                break;
            }
        }
        res = res.substring(inx);
        System.out.println(res.isEmpty() ? "0" : res);
    }


    @Test
    public void dailyTemperatures() {
        int[] temperatures = {2, 3, 4, 1, 55};
        int[] result = new int[temperatures.length];

        for (int i = temperatures.length - 2; i >= 0; i--) {
            int t = temperatures[i];
            for (int j = i + 1; j < temperatures.length; j = j + result[j]) {
                if (t < temperatures[j]) {
                    result[i] = j - i;
                    break;
                } else if (result[j] == 0) {
                    result[i] = 0;
                    break;
                }
            }
        }

        // 方法二：使用栈，从后往前比较
//        Stack<Integer> stack = new Stack<Integer>();
//        int n = temperatures.length;
//        int[] res= new int[n];
//        for(int i = n-1;i>=0;i--){
//            while(!stack.isEmpty() && temperatures[stack.peek()] <= temperatures[i]){
//                stack.pop();
//            }
//            res[i] = stack.isEmpty()?0:stack.peek()-i;
//            stack.push(i);
//        }
    }

//    Deque<Integer> deque;
//    Queue<Integer> queue;
//
//    public int getMax() {
//        if (deque.isEmpty()) {
//            return -1;
//        }
//        return deque.pop();
//    }
//
//    public void pushLast(int val) {
//        if (!deque.isEmpty() && deque.peekFirst() < val) {
//            deque.pollLast();
//        }
//        deque.offerLast(val);
//        queue.offer(val);
//    }
//
//    public int pop_front() {
//        if (queue.isEmpty()) {
//            return -1;
//        }
//        Integer res = queue.poll();
//        if (res == deque.peekFirst()) {
//            deque.pollFirst();
//        }
//        return res;
//    }

    @Test
    public void minCost(int[] cost) {
        int dp0 = 0;
        int dp1 = 0;
        for (int i = 2; i <= cost.length; i++) {
            int dpi = Math.min(dp1 + cost[i - 1], dp0 + cost[i - 2]);
            dp0 = dp1;
            dp1 = dpi;
        }
        System.out.println(dp1);
    }

    @Test//零钱兑换 所需的最小硬币数
    public void minCoinNumber() {
        int[] coins = {1, 2, 5};
        int amount = 11;
        int[] dp = new int[amount + 1];
        int max = amount + 1;
        Arrays.fill(dp, max);
        dp[0] = 0;

        for (int i = 1; i <= amount; i++) {
            for (int j = 0; j < coins.length; j++) {
                if (coins[j] <= i) {
                    dp[i] = Math.min(dp[i], dp[i - coins[j]] + 1);
                    System.out.println(dp[i]);
                }
            }
        }
        System.out.println(dp[amount] > amount ? -1 : dp[amount]);

    }

    @Test//零钱兑换的组合数
    public void coinReplace() {
//        输入: coins = [1, 2, 5], amount = 11
//        输出: 3
//        解释: 11 = 5 + 5 + 1
//        11 = dp[i] + dp[11-dp[i]];
        int[] coins = {1, 2, 5};
        int amount = 5;
        //递推表达式
        int[] dp = new int[amount + 1];
        //初始化dp数组，表示金额为0时只有一种情况，也就是什么都不装
        dp[0] = 1;
        // 遍历物品
        for (int i = 0; i < coins.length; i++) {
            // 遍历容量
            for (int j = coins[i]; j <= amount; j++) {
                dp[j] = dp[j] + dp[j - coins[i]];
                // dp[1] = dp[1] +dp[1-1]
                // dp[2] = dp[2] + dp[2-2]
                // dp[3] = dp[3] + dp[3-2]
                // dp[4] = dp[4] + dp[4-2]
                // dp[5] = dp[5] + dp[5-2]
                System.out.println(dp[j]);
            }
        }
        System.out.println(dp[amount]);
    }

    @Test
    public void longestIncreasingSequence(int[] arr) {
        //在这个状态转移方程中，dp[i]表示以nums[i]结尾的最长上升子序列长度，dp[j]表示以nums[j]结尾的最长上升子序列长度，
        // 而nums[j] < nums[i]，说明可以将以nums[j]结尾的最长上升子序列接上nums[i]，形成一个更长的上升子序列，长度为dp[j] + 1。
        // 因此，dp[i]的值需要取dp[i]和dp[j]+1两者中的较大值，以保证dp[i]保存的是最长的上升子序列长度。
        int[] dp = new int[arr.length + 1];
        int res = 1;
        for (int i = 0; i < arr.length; i++) {
            dp[i] = 1;
            for (int j = 0; j < i; j++) {
                if (arr[i] > arr[j]) {
                    dp[i] = Math.max(arr[i], arr[j] + 1);
                }
            }
            res = Math.max(res, dp[i]);
        }
        System.out.println(res);
    }

    @Test
    public void bagQuestion() {
        int[] weight = {1, 3, 4};
        int[] value = {15, 20, 30};
        int bagWeight = 4;
        helpBagSize(weight, value, bagWeight);

    }

    public void helpBagSize(int[] weight, int[] values, int bagWeight) {
        // 有n件物品和一个最多能背重量为w 的背包。第i件物品的重量是weight[i]，得到的价值是value[i] 。
        // 每件物品只能用一次，求解将哪些物品装入背包里物品价值总和最大。
        // 关键！ 动态规划记住dp数组的定义：dp[i][j] 从0-i物品中选，放入j中的背包中
        int[][] dp = new int[weight.length][bagWeight + 1];
        for (int i = weight[0]; i < bagWeight; i++) {
            dp[0][i] = values[0];
        }
        for (int i = 1; i < weight.length; i++) {
            for (int j = 1; j <= bagWeight; j++) {
                if (j < weight[i]) dp[i][j] = dp[i - 1][j];
                else dp[i][j] = Math.max(dp[i - 1][j], dp[i - 1][j - weight[i]] + values[i]);
            }
        }
        for (int i = 0; i < weight.length; i++) {
            for (int j = 0; j <= bagWeight; j++) {
                System.out.print(dp[i][j] + "\t");
            }
            System.out.println();
        }
    }

    @Test
    public void threeSum() {
        int[] nums = {-1, 0, 1, 2, -1, -4};
        Arrays.sort(nums);
        List<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            int curr = nums[i];
            // 如果等于前一个数字，跳过
            if (i > 0 && curr == nums[i - 1]) continue;
            // 当n大于0时，不会再找到合理解，结束循环
            if (curr > 0) break;
            int left = i + 1;
            int right = nums.length - 1;
            while (left < right) {

                int l = nums[left];
                int r = nums[right];
                int sum = l + r + curr;
                if (sum == 0) {
                    result.add(Arrays.asList(curr, l, r));
                    while (left < right && l == nums[left]) left++;
                    while (left < right && r == nums[right]) right--;
                }
                if (sum < 0) {
                    left++;
                }
                if (sum > 0) {
                    right--;
                }
            }


        }
        for (int i = 0; i < result.size(); i++) {
            System.out.println(JSON.toJSONString(result.get(i)));
        }
    }

    @Test
    public void longestIncreasingSubsequence() {
        // 动规1
        //  nums = [10,9,2,5,3,7,101,18] 最长递增子序列
        // 什么是动态规划：将大问题拆分成一个个小问题，直到小问题可以被解决，然后在小问题的基础上一步步向上反推结果
        // 动态规划基本思路：自底向上穷举分析---确定边界---找规律,确定最优子结构---确定状态转移方程（在编码过程中，时刻记住转移方程代表的意思是什么）
        int[] nums = {10, 9, 2, 5, 3, 7, 101, 18};
        int res = 1;
        int[] dp = new int[nums.length];
        dp[0] = 1;
        for (int i = 1; i < nums.length; i++) {
            dp[i] = 1;
            for (int j = 0; j < i; j++) {
                if (nums[j] < nums[i]) {
                    dp[i] = Math.max(dp[i], dp[j] + 1);
                }
            }
            res = Math.max(res, dp[i]);
        }
        System.out.println(res);
    }

    /*
    public void longIncreaseSubSequence(int[] arr) {
            int res = 0;
            int[] dp = new int[arr.length];
            for(int i = 1;i<arr.length();i++){
                dp[i] = 1;
                for(int j = 0;j<i;j++){
                    if(arr[j]<arr[i]){
                    dp[i] = Math.max(dp[i],dp[j]+1);
                    }
                }
                res = Math.max(res,dp[i]);
            }
    }
     */
    @Test
    public void longestCommonSubstring() {
        // 动规2
        // 两个字符串，求出最长公共子串
        //由于 dp[i][j] 的含义是 text1[0:i-1] 和 text2[0:j-1] 的最长公共子序列。我们最终希望求的是 text1 和 text2 的最长公共子序列。所以需要返回的结果是 i = len(text1) 并且 j = len(text2) 时的 dp[len(text1)][len(text2)]。
        // 用一句通俗的话来描述这种dp[i][j]规律，就是相等左上角加一，取最大值，
        // 如果不等取只置为0。

        String s1 = "ghjk123pp";
        String s2 = "nhgjk123pp";
        int m = s1.length();
        int n = s2.length();
        int[][] dp = new int[m + 1][n + 1];
        int res = 0;
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                    res = Math.max(res, dp[i][j]);
                } else {
                    dp[i][j] = 0;
                }
            }
        }
        System.out.println(res);
    }

    @Test
    public void longestCommonSubsequence() {
        // 动规3
        // 两个字符串，求出最长公共子序列
        //输入：text1 = "abcde", text2 = "ace"
        //输出：3
        //解释：最长公共子序列是 "ace" ，它的长度为 3
        //由于 dp[i][j] 的含义是 text1[0:i-1] 和 text2[0:j-1] 的最长公共子序列。我们最终希望求的是 text1 和 text2 的最长公共子序列。所以需要返回的结果是 i = len(text1) 并且 j = len(text2) 时的 dp[len(text1)][len(text2)]。
        //当 text1[i - 1] == text2[j - 1] 时，说明两个子字符串的最后一位相等，所以最长公共子序列又增加了 1，所以 dp[i][j] = dp[i - 1][j - 1] + 1；举个例子，比如对于 ac 和 bc 而言，他们的最长公共子序列的长度等于 a 和 b 的最长公共子序列长度 0 + 1 = 1。
        //当 text1[i - 1] != text2[j - 1] 时，说明两个子字符串的最后一位不相等，那么此时的状态 dp[i][j] 应该是 dp[i - 1][j] 和 dp[i][j - 1] 的最大值。举个例子，比如对于 ace 和 bc 而言，他们的最长公共子序列的长度等于 ① ace 和 b 的最长公共子序列长度0 与 ② ac 和 bc 的最长公共子序列长度1 的最大值，即 1
        // 用一句通俗的话来描述这种dp[i][j]规律，就是相等左上角加一，不等取上或左最大值，如果上左一样大，优先取左。
        String s1 = "ghjk123pp";
        String s2 = "nhgjk123gpg";
        int m = s1.length();
        int n = s2.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        System.out.println(dp[m][n]);
    }


    @Test
    // 最大温度
    public void temperatures(int[] temperatures) {
        //给定一个整数数组 temperatures ，表示每天的温度，返回一个数组 answer ，
        // 其中 answer[i] 是指对于第 i 天，下一个更高温度出现在几天后。
        // 如果气温在这之后都不会升高，请在该位置用 0 来代替。
        int[] result = new int[temperatures.length];
        for (int i = temperatures.length - 2; i >= 0; i--) {
            int t = temperatures[i];
            for (int j = i + 1; j < temperatures.length; j = j + result[j]) {
                if (t < temperatures[j]) {
                    result[i] = j - i;
                    break;
                } else if (result[j] == 0) {
                    result[i] = 0;
                    break;
                }
            }
        }
    }


    @Test
    // 给定一个字符串数组，求最长公共前缀
    public void longestCommonprefix(String[] arrays) {
        Arrays.sort(arrays);
        String a1 = arrays[0];
        String a2 = arrays[arrays.length];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arrays[0].length(); i++) {
            if (a1.charAt(i) == a2.charAt(i)) {
                sb.append(a1.charAt(i));
            } else break;
        }
    }

    @Test
    public void validIsPalindrome(String s) {
        String s1 = s.replaceAll("\\s+", "");
        char[] chars = s1.toCharArray();
        int l = 0;
        int r = chars.length;
        while (l < r) {
            if (!Character.isLetterOrDigit(chars[l])) {
                l++;
            } else if (!Character.isLetterOrDigit(chars[r])) {
                r--;
            } else {
                if (Character.toLowerCase(chars[l]) != Character.toLowerCase(chars[r])) {
                    System.out.println(false);
                }
                l++;
                r--;

            }
        }
        System.out.println(true);
    }

    @Test
    public void longestPalindromeSubString(String s) {
        int res = 0;
        if (s.length() < 2) {
            System.out.println(false);
        }
        for (int i = 1; i < s.length(); i++) {
            String s1 = palindromeHelp(s, i, i);
            String s2 = palindromeHelp(s, i - 1, i + 1);
            res = s1.length() > res ? s1.length() : res;
            res = s2.length() > res ? s2.length() : res;
        }
        System.out.println(res);
    }

    public String palindromeHelp(String s, int left, int right) {
        while (left > 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            left--;
            right++;
        }

        return s.substring(left + 1, right - left - 1);

    }


    @Test
    public void getMinStack() {
        // 借助一个辅助栈helpStack，如果入栈的元素值小于辅助栈顶的元素，则直接入栈
        // 反之，重复入栈 辅助栈顶的元素
        // 出栈，stack1和helpStack正常出栈
        //    public void push(int val) {
//        data.add(val);
//        if(helper.isEmpty() || helper.peek()>=val){
//            helper.add(val);
//        }else{
//            helper.add(helper.peek());
//        }
//    }
    }


    @Test
    // 无重复字符的最长子串
    public void noRepeatLongestCommonSubString() {
        String words = "abdcdd";
        HashSet<Character> set = new HashSet<>();
        int result = 0, i = 0, j = 0;
        int n = words.length();
        // 设置一个滑动窗口，如果集合不包含该元素，则右指针滑动，否则，移除左指针的元素
        // 每个过程保留最大值
        while (i < n && j < n) {
            if (!set.contains(words.charAt(j))) {
                set.add(words.charAt(j));
                j++;
                result = Math.max(result, j - i);
            } else {
                set.remove(words.charAt(i));
                i++;
            }
        }
        System.out.println(result);
    }

    @Test
    public void asdasd() {
        int[][] arr = new int[][]{{1, 2}, {5, 6}, {99, 100}};
        System.out.println(JSON.toJSONString(arr));
    }

    // 不实用额外的空间，去除数组中重复的元素
    public int deleteRepeatNum(int[] arr) {
        int n = arr.length;
        int slow = 0;
        int fast = 1;
        while (fast < n) {
            if (arr[slow] != arr[fast]) {
                slow++;
                arr[slow] = arr[fast];
            }
            fast++;
        }
        return slow + 1;

    }




    @Test
    public void quickSort(int[] arrs, int low, int high) {
        if (low < high) {
            int pointer = partition(arrs, low, high);
            quickSort(arrs, low, pointer - 1);
            quickSort(arrs, pointer + 1, high);
        }
    }

    public int partition(int[] arr, int low, int high) {
        int basePoint = arr[high];
        int pointer = low;
        for (int i = low; i < high; i++) {
            if (arr[i] < basePoint) {
                swap(arr, i, pointer++);
            }
        }
        swap(arr, pointer, high);
        return pointer;
    }

    public void swap(int[] arr, int x, int y) {
        int tmp = arr[x];
        arr[x] = arr[y];
        arr[y] = tmp;
    }

    @Test
    // 返回最小的k个数
    // 构建一个大顶堆，遍历arr元素，如果优先队列的容量大于k，则弹出该队列中最大的值，剩下就为最小的k个元素
    public void getLeastNumbers(int[] arr,int k) {
        PriorityQueue<Integer> queue = new PriorityQueue<>(Collections.reverseOrder());
        for(int num : arr){
            queue.add(num);
            if(queue.size() > k){
                queue.poll();
            }
        }
        int[] resArr = new int[k];
        for(int i=0;i<k;i++){
            resArr[k-i-1] = queue.poll();
        }
        System.out.println(JSON.toJSONString(resArr));
    }

    @Test
    // 单词拆分
    // 给你一个字符串 s 和一个字符串列表 wordDict 作为字典。
    // 请你判断是否可以利用字典中出现的单词拼接出 s
    //注意：不要求字典中出现的单词全部都使用，并且字典中的单词可以重复使用。
    // 经典的动态规划 dp[i] 代表字符串的前i个字符能否被拆分成字典中的元素
    // ，s.substring(j, i) 表示从字符串 s 的第 j 个字符到第 i-1 个字符构成的子串，wordDict.contains(s.substring(j, i)) 表示该子串是否在 wordDict 中出现过。
    public void wordBreak(String s,List<String> wordDict) {
        Boolean[] dp = new Boolean[wordDict.size()+1];
        dp[0] = true;
        for (int i = 0; i < s.length(); i++) {
            for (int j = 0; j < i; j++) {
                if(dp[j] && wordDict.contains(s.substring(j,i))){
                    dp[i] = true;
                    break;
                }
            }
        }
    }


    @Test
    public void fourSum(int[] nums, int target) {
    // 给你一个由 n 个整数组成的数组 nums ，和一个目标值 target 。请你找出并返回满足下述全部条件且不重复的四元组 [nums[a], nums[b], nums[c], nums[d]] （若两个四元组元素一一对应，则认为两个四元组重复）：
//        0 <= a, b, c, d < n
//        a、b、c 和 d 互不相同
//        nums[a] + nums[b] + nums[c] + nums[d] == target
//        你可以按 任意顺序 返回答案 。
//
//
            List<List<Integer>> res  =  new ArrayList<>();
            Arrays.sort(nums); // O(nlogn)
            int len = nums.length;
            for(int i = 0; i < len - 3; i++) {   // O(n^3)
                if (i > 0 && nums[i] == nums[i-1]) {
                    continue; // 跳过重复
                }
                for (int j = i + 1; j < len - 2; j++) { // same as threeSum  O(n^2)
                    if (j > i + 1 && nums[j] == nums[j-1]) {
                        continue; // 跳过重复
                    }
                    int left = j + 1;
                    int right = len - 1;
                    while (left < right) {
                        long sum=(long) nums[i]+nums[j]+nums[left]+nums[right];
                        //int sum = nums[i] + nums[j] + nums[left] + nums[right];
                        if (sum == target) {
                            res.add(Arrays.asList(nums[i], nums[j], nums[left], nums[right]));

                            // 跳过重复, 可以先不看
                            while(left < right && nums[left+1] == nums[left]) left++;
                            while (left < right && nums[right-1] == nums[right]) right--;

                            // 逼近中间
                            left++;
                            right--;
                        } else if (sum > target) {
                            right--;
                        } else { // sum < target
                            left++;
                        }
                    }
                }
            } }


    @Test
    public void callGPT(){
        String question = "给我讲一个冷笑话";
        String response = getResponse(question);
        System.out.println("结果是"+response);

    }
    // 调用OpenAI的chatGPT接口的Java代码
    public static String sendRequest(String question) {
        String prompt = question;
        String apiKey = "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";
        String apiUrl = "https://api.openai.com/v1/engines/davinci-codex/completions";
        int maxTokens = 1000;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setDoOutput(true);

            String jsonInputString = String.format("{\"prompt\": \"%s\", \"max_tokens\": %d}", prompt, maxTokens);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed : HTTP error code : " + connection.getResponseCode());
            }

            try (Scanner scanner = new Scanner(connection.getInputStream(), StandardCharsets.UTF_8.name())) {
                String response = scanner.useDelimiter("\\A").next();
                System.out.println(response);
                return response;
            }
//            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }


    public static String getResponse(String question) {
        String response = sendRequest(question);
        Gson gson = new Gson();

        // 解析 JSON 响应
        ChatGPTResponse chatGPTResponse = gson.fromJson(response, ChatGPTResponse.class);

        // 获取答案
        List<ChatGPTChoice> choices = chatGPTResponse.getChoices();
        String answer = choices.get(0).getText();

        return answer;
    }

    class ChatGPTResponse {
        private List<ChatGPTChoice> choices;

        public List<ChatGPTChoice> getChoices() {
            return choices;
        }

        public void setChoices(List<ChatGPTChoice> choices) {
            this.choices = choices;
        }
    }

    class ChatGPTChoice {
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
