package org.blackwhite.share.model;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.shiro.crypto.hash.Sha1Hash;
import org.blackwhite.share.util.CollectionUtils;
import org.blackwhite.share.util.ModelUtils;
import org.blackwhite.share.util.StringUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

public class UserModel extends Model<UserModel>{

	private static final long serialVersionUID = -6726590608690137373L;
	public static final UserModel dao = new UserModel();
	
	public UserModel findByUsername(String username){
		String sql = "select * from user where username = ?";
		return findFirst(sql,username);
	}

	public boolean checkPwd(String password){
		String sysPassword = getStr("password");
		String salt = getStr("salt");
		String tPassword = new Sha1Hash(password, salt).toHex();
		return sysPassword.equalsIgnoreCase(tPassword);
	}
	
	public boolean add(String username, String password) {
		UserModel user = new UserModel();
		String salt = StringUtils.rand(6);
		String tPassword = new Sha1Hash(password, salt).toHex();
		user.set("username", username)
			.set("password", tPassword)
			.set("salt", salt)
			.set("createTime", new Date());
		return user.save();
	}

	public boolean exist(String username) {
		String sql = "select count(id) from user where username = ? ";
		Long cnt = Db.queryLong(sql,username);
		return cnt != null && cnt > 0;
	}

	public List<UserModel> listByIds(List<Integer> userIds) {
		if(CollectionUtils.isBlank(userIds)){
			return new LinkedList<UserModel>();
		}
		String sql = "select id,avatar from user where id in (??)";
		sql = ModelUtils.buildSqlIn(sql, "??", userIds);
		List<UserModel> list = find(sql);
		return CollectionUtils.toSafeList(list);
	}
	
	
}
