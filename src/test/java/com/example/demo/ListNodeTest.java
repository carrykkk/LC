package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ListNodeTest.class)
@Slf4j
public class ListNodeTest {

    @Test
    // 反转链表
    public ListNode reverseLink(ListNode head) {
        ListNode p = null, pre = null;
        while (head != null) {
            p = head.next;
            head.next = pre;
            pre = head;
            head = p;
        }
        return pre;
    }

    @Test
    // K个一组翻转链表
    public ListNode reverseKGroup(ListNode head, int k) {
        // 使用递归的思想来解决，每次反转[a,b)之间的链表，递归反转 后续链表在连接起来
        // 1->2->3->4->5
        // 2->1->4->3->5
        // https://zhuanlan.zhihu.com/p/90170262
        ListNode a = head, b = head;
        for (int i = 0; i < k; i++) {
            // 包含头节点在内的k个一组，这里是让b遍历到第k+1个节点位置
            if (b != null) {
                b = b.next;
            } else {
                return head;
            }
        }
        ListNode newHead = reverseAToB(a, b);
        a.next = reverseKGroup(b, k);
        return newHead;
    }

    @Test
    // 反转a-b之间的链表[a,b)
    public ListNode reverseAToB(ListNode a, ListNode b) {
        ListNode pre = null, p = null;
        while (a != b) {
            p = a.next;
            a.next = pre;
            pre = a;
            a = p;
        }
        return pre;
    }

    @Test
    // 链表重排序
    public void reorderListNode(ListNode head){
        // 给定一个单链表 L 的头节点 head ，单链表 L 表示为：
        // L0 → L1 → … → Ln - 1 → Ln
        // 请将其重新排列后变为：
        // L0 → Ln → L1 → Ln - 1 → L2 → Ln - 2 → …
        // 基本思路,先使用快慢指针找到中间节点，将链表分成两段，然后进行翻转后一段链表，最后将两段链表合并
        ListNode slow = head,fast =head;
        // 实用快慢指针找到中间节点
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        ListNode pre = null, cur = slow.next;
        slow.next = null;
        // 翻转后一段链表
        while (cur != null) {
            ListNode tmp = cur.next;
            cur.next = pre;
            pre = cur;
            cur = tmp;
        }
        //合并两段链表
        ListNode p = head,q = pre;
        while (q != null) {
            ListNode next1 = p.next, next2 = q.next;
            p.next = q;
            q.next = next1;
            p = next1;
            q = next2;
        }

    }

    @Test
    // 删除倒数第N个节点
    public void deleteNNode(ListNode head, int n) {
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode first = dummy;
        ListNode second = dummy;
        for (int i = 1; i <= n + 1; i++) {
            first = first.next;
        }
        while (first != null) {
            first = first.next;
            second = second.next;
        }
        second.next = second.next.next;

    }

    @Test
    // 两个链表求和
    public void twoListNodeSum(ListNode p1, ListNode p2) {
        ListNode dummy = null;
        int carry = 0;
        while (p1 != null || p2 != null) {
            int x1 = p1 != null ? p1.value : 0;
            int x2 = p2 != null ? p2.value : 0;
            int sum = carry + x1 + x2;
            carry = sum / 10;
            dummy.next = new ListNode(sum % 10);
            dummy = dummy.next;
            if (p1 != null) p1 = p1.next;
            if (p2 != null) p2 = p2.next;
        }
        if (carry > 0) dummy.next = new ListNode(carry);
        System.out.println(dummy);
    }

    @Test
    // 合并两个有序链表 LC21
    public ListNode mergeListNode(ListNode p1, ListNode p2){
        ListNode dummy = new ListNode(0);
        ListNode cur = dummy;
        while (p1 != null && p2 != null) {
            if (p1.value > p2.value) {
                cur.next = p1;
                p1 = p1.next;
            }else{
                cur.next = p2;
                p2 = p2.next;
            }
            cur = cur.next;
        }
        if (p1 != null) {
            cur.next = p1;
        }
        if(p2 != null){
            cur.next = p2;
        }
        return dummy.next;
    }

    //分割链表
    @Test
    public ListNode partition(ListNode head,int x){
        //  给你一个链表的头节点 head 和一个特定值 x ，请你对链表进行分隔，使得所有 小于 x 的节点都出现在 大于或等于 x 的节点之前。
        //  你应当 保留 两个分区中每个节点的初始相对位置。
        //  输入：head = [1,4,3,2,5,2], x = 3
        //  输出：[1,2,2,4,3,5]
        //  基本思路：在合并两个有序链表时让你合二为一，而这里需要分解让你把原链表一分为二。具体来说，我们可以把原链表分成两个小链表，一个链表中的元素大小都小于 x，另一个链表中的元素都大于等于 x，最后再把这两条链表接到一起，就得到了题目想要的结果。
        ListNode dummy1 = new ListNode(0);
        ListNode dummy2 = new ListNode(0);
        ListNode p1 = dummy1,p2 = dummy2;
        ListNode p = head;
        while(p != null){
            if(p.value >= x){
                p2.next = p;
                p2 = p2.next;
            }else{
                p1.next = p;
                p1 = p1.next;
            }
            ListNode temp = p.next;
            p.next = null;
            p = temp;
        }
        p1.next = dummy2.next;
        return dummy1.next;
    }


    // 链表是否有环
    @Test
    public boolean isCycle(ListNode head) {
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) {
                return true;
            }
        }
        return false;
    }

    // 如果链表中含有环，如何计算这个环的起点
    @Test
    public ListNode findCycleStart(ListNode head) {
        // 基本思路：假设快慢指针相遇时，慢指针 slow 走了 k 步，那么快指针 fast 一定走了 2k 步：
        // fast 一定比 slow 多走了 k 步，这多走的 k 步其实就是 fast 指针在环里转圈圈，所以 k 的值就是环长度的「整数倍」。
        // 假设相遇点距环的起点的距离为 m，那么结合上图的 slow 指针，环的起点距头结点 head 的距离为 k - m，也就是说如果从 head 前进 k - m 步就能到达环起点。
        // 巧的是，如果从相遇点继续前进 k - m 步，也恰好到达环起点。因为结合上图的 fast 指针，从相遇点开始走k步可以转回到相遇点，那走 k - m 步肯定就走到环起点了：
        ListNode fast, slow;
        fast = slow = head;
        while (fast != null && fast.next != null) {
            fast = fast.next.next;
            slow = slow.next;
            if (fast == slow) break;
        }
        // 上面的代码类似 hasCycle 函数
        if (fast == null || fast.next == null) {
            // fast 遇到空指针说明没有环
            return null;
        }

        // 重新指向头结点
        slow = head;
        // 快慢指针同步前进，相交点就是环起点
        while (slow != fast) {
            fast = fast.next;
            slow = slow.next;
        }
        return slow;
    }

    // 判断两个链表是否相交
    @Test
    public ListNode getIntersectionNode(ListNode headA,ListNode headB) {
        ListNode p1 = headA,p2 = headB;
        while (p1 != p2) {
            if (p1 == null) p1 = headB;
            else p1 = p1.next;
            if (p2 == null) p2 = headA;
            else p2 = p2.next;
        }
        return p1;
    }


    public class ListNode {
        int value;
        ListNode next = null;
        ListNode(int x) {
            this.value = x;
        }
    }


}
