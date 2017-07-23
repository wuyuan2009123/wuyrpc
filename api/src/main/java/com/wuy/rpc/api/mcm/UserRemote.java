package com.wuy.rpc.api.mcm;

import com.wuy.rpc.api.pojo.User;

import java.util.List;




public interface UserRemote {
	Object save(User user);
	Object saves(List<User> users);
	Object add(int a, float b);
}
