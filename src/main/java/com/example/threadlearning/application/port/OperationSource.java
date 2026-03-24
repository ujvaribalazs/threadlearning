package com.example.threadlearning.application.port;

import com.example.threadlearning.domain.models.BankOperation;
import com.example.threadlearning.domain.models.FamilyMember;

public interface OperationSource {
    BankOperation nextOperation(FamilyMember familyMember);
}
