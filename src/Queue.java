package src;
// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

final class Queue {

    public Queue()
    {
        head = new NodeSub();
        head.prevNodeSub = head;
        head.nextNodeSub = head;
    }

    public void insertBack(NodeSub nodeSub)
    {
        if(nodeSub.nextNodeSub != null)
            nodeSub.unlinkSub();
        nodeSub.nextNodeSub = head.nextNodeSub;
        nodeSub.prevNodeSub = head;
        nodeSub.nextNodeSub.prevNodeSub = nodeSub;
        nodeSub.prevNodeSub.nextNodeSub = nodeSub;
    }

    public NodeSub popTail()
    {
        NodeSub nodeSub = head.prevNodeSub;
        if(nodeSub == head)
        {
            return null;
        } else
        {
            nodeSub.unlinkSub();
            return nodeSub;
        }
    }

    public NodeSub getFront()
    {
        NodeSub nodeSub = head.prevNodeSub;
        if(nodeSub == head)
        {
            current = null;
            return null;
        } else
        {
            current = nodeSub.prevNodeSub;
            return nodeSub;
        }
    }

    public NodeSub getNext()
    {
        NodeSub nodeSub = current;
        if(nodeSub == head)
        {
            current = null;
            return null;
        } else
        {
            current = nodeSub.prevNodeSub;
            return nodeSub;
        }
    }

    public int getSize()
    {
        int i = 0;
        for(NodeSub nodeSub = head.prevNodeSub; nodeSub != head; nodeSub = nodeSub.prevNodeSub)
            i++;

        return i;
    }

    private final NodeSub head;
    private NodeSub current;
}
