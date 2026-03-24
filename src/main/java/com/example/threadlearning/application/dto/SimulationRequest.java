package com.example.threadlearning.application.dto;

import com.example.threadlearning.domain.models.FamilyMember;

import java.util.List;

public record SimulationRequest(
        List<FamilyMember> familyMembers,
        int operationsPerMember
) {
}