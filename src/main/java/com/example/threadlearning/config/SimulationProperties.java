package com.example.threadlearning.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "simulation")
public class SimulationProperties {

    private int memberCount;
    private int operationsPerMember;
    private int initialBalance;
    private int minAmount;
    private int maxAmount;
    private String runnerType;
    private int poolSize;

    public int getMemberCount() { return memberCount; }
    public void setMemberCount(int memberCount) { this.memberCount = memberCount; }

    public int getOperationsPerMember() { return operationsPerMember; }
    public void setOperationsPerMember(int operationsPerMember) { this.operationsPerMember = operationsPerMember; }

    public int getInitialBalance() { return initialBalance; }
    public void setInitialBalance(int initialBalance) { this.initialBalance = initialBalance; }

    public int getMinAmount() { return minAmount; }
    public void setMinAmount(int minAmount) { this.minAmount = minAmount; }

    public int getMaxAmount() { return maxAmount; }
    public void setMaxAmount(int maxAmount) { this.maxAmount = maxAmount; }

    public String getRunnerType() { return runnerType; }
    public void setRunnerType(String runnerType) { this.runnerType = runnerType; }

    public int getPoolSize() { return poolSize; }
    public void setPoolSize(int poolSize) {this.poolSize = poolSize;}

}