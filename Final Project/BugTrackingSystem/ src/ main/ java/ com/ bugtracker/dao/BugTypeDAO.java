package com.bugtracker.dao;

import com.bugtracker.model.BugType;
import java.util.List;

public interface BugTypeDAO {
    BugType getBugTypeByCode(int bugCode);
    List<BugType> getAllBugTypes();
    // Add other methods if needed, but for this project, primarily read operations
}
