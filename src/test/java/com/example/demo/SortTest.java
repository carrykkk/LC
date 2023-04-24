package com.example.demo;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.PriorityQueue;

@SpringBootTest(classes = SortTest.class)
@Slf4j
public class SortTest {


    @Test
    public void bubbleSort() {
        int[] arr = {12, 32, 45};
        for (int i = 1; i < arr.length; i++) {
            boolean flag = true;
            for (int j = 0; j < arr.length - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    int tmp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = tmp;
                    flag = false;
                }
            }
            if (flag) {
                break;
            }
        }
        System.out.println(JSON.toJSONString(arr));
    }

    @Test
    public void selectSort() {
        //基本思路是将待排序的元素分为已排序和未排序两部分，每次从未排序的部分中选择最小（或最大）的元素，将其放置在已排序部分的末尾，直到所有元素都被排序为止。
        int[] arrs = {12, 32, 43};
        for (int i = 0; i < arrs.length - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < arrs.length; j++) {
                if (arrs[j] < arrs[minIndex]) {
                    minIndex = j;
                }
            }
            if (minIndex != i) {
                int tmp = arrs[i];
                arrs[i] = arrs[minIndex];
                arrs[minIndex] = tmp;
            }
        }
    }

    @Test
    public void insertSort() {
        // 将待排序的元素分为已排序和未排序两部分，初始时将第一个元素看作已排序部分，其余元素看作未排序部分。每次从未排序部分中取出第一个元素，将它插入已排序部分的合适位置，然后重复这个过程，直到未排序部分为空。
        int[] arr = {99, 23, 13, 222};
        for (int i = 1; i < arr.length; i++) {
            int preIndex = i - 1;
            int curr = arr[i];
            while (preIndex >= 0 && curr < arr[preIndex]) {
                arr[preIndex + 1] = arr[preIndex];
                preIndex--;
            }
            arr[preIndex + 1] = curr;
        }
        Arrays.stream(arr).forEach(System.out::println);
    }

    @Test
    public int binarySearch(int[] arr, int l, int r, int k) {
        if (l > r) {
            return -1;
        }
        int mid = (l + r) / 2;
        if (arr[mid] == k) {
            return mid;
        } else if (arr[mid] < k) {
            return binarySearch(arr, mid + 1, r, k);
        } else {
            return binarySearch(arr, l, mid - 1, k);
        }
    }


    @Test
    public void quickSort(int[] arrs, int low, int high) {
        if (low < high) {
            int pointer = partition(arrs, low, high);
            quickSort(arrs, low, pointer - 1);
            quickSort(arrs, pointer + 1, high);
        }
    }

    @Test
    // 找第k大的元素
    // 方法一：快速排序查找算法
    public int findKthLargestOne(int[] nums, int k) {
        if (nums == null || nums.length == 0 || k < 0 || k > nums.length) {
            return -1;
        }
        return quickSelect(nums, 0, nums.length - 1, k);
    }

    @Test
    // 找第K大的元素
    // 方法二：使用优先队列 PriorityQueue（默认是小顶堆）
    public int findKthLargestTwo(int[] nums, int k) {
        PriorityQueue<Integer> queue = new PriorityQueue<>();
        for (int num : nums) {
            if (queue.size() < k) {
                queue.add(num);
                continue;
            }
            if (num > queue.peek()) {
                queue.poll();
                queue.add(num);
            }
        }
        return queue.poll();
    }

    public int quickSelect(int[] nums, int low, int hight, int k) {
        if (low < hight) {
            int partition = partition(nums, low, hight);
            if (partition == k) {
                return nums[k];
            } else if (partition > k) {
                return quickSelect(nums, low, partition - 1, k);
            } else {
                return quickSelect(nums, partition + 1, hight, k);
            }
        }
        return -1;
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

}
