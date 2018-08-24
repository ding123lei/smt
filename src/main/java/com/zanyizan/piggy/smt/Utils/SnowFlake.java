package com.zanyizan.piggy.smt.Utils;


public class SnowFlake {

    private final static long START_STAMP = 1480166465631L;

    private final static long SEQUENCE_BIT = 12;
    private final static long MACHINE_BIT = 6;
    private final static long DATACENTER_BIT = 6;

    private final static long MAX_DATACENTER_NUM = ~(-1L << DATACENTER_BIT);
    private final static long MAX_MACHINE_NUM = ~(-1L << MACHINE_BIT);
    private final static long MAX_SEQUENCE = ~(-1L << SEQUENCE_BIT);

    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    private final static long TIMESTAMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;

    private long dataCenterId;
    private long machineId;
    private long sequence = 0L;
    private long lastStamp = -1L;

    public SnowFlake(long dataCenterId, long machineId) {
        dataCenterId = Math.abs(dataCenterId) % MAX_DATACENTER_NUM + 1;
        machineId = Math.abs(machineId) % MAX_MACHINE_NUM + 1;

        if (dataCenterId > MAX_DATACENTER_NUM || dataCenterId < 0) {
            throw new IllegalArgumentException("dataCenterId can't be greater than MAX_DATACENTER_NUM or less than 0");
        }
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
        }
        this.dataCenterId = dataCenterId;
        this.machineId = machineId;
    }

    public synchronized long nextId() {
        long currStamp = getNewStamp();
        if (currStamp < lastStamp) {
            throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
        }

        if (currStamp == lastStamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0L) {
                currStamp = getNextMill();
            }
        } else {
            sequence = 0L;
        }

        lastStamp = currStamp;

        return (currStamp - START_STAMP) << TIMESTAMP_LEFT
                | dataCenterId << DATACENTER_LEFT
                | machineId << MACHINE_LEFT
                | sequence;
    }

    private long getNextMill() {
        long mill = getNewStamp();
        while (mill <= lastStamp) {
            mill = getNewStamp();
        }
        return mill;
    }

    private long getNewStamp() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) {
        SnowFlake snowFlake1 = new SnowFlake(1, 100);
        System.out.println(snowFlake1.nextId());
        System.out.println("bj1a".hashCode());
        System.out.println("bj1a".hashCode() % MAX_DATACENTER_NUM + 1);

        System.out.println("bj1b".hashCode());
        System.out.println("bj1b".hashCode() % MAX_DATACENTER_NUM + 1);

        System.out.println("bj1d".hashCode());
        System.out.println("bj1d".hashCode() % MAX_DATACENTER_NUM + 1);
    }
}