package com.tecomgroup.qos.rest;

import com.tecomgroup.qos.rest.data.*;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.InputStream;
import java.util.*;

/**
 *
 * @author stroganov.d
 */
public interface ChannelService {


	public ChannelsStateResponse getCommonChannelsSates(boolean includesConfiguration);

	public ChannelStateCommon getDetailedChannelSate(long channelId);

	public SetConfiguration getChannelById(Long channelId);

	public SetConfiguration[] getAllUserChannels();

	public SetConfiguration[] getAllUserSets();

	public Long createUserChannel(SetConfiguration channelConfig) ;

	public Long updateChannel(SetConfiguration channelConfig);

	public boolean deleteChannelById(Long id);

	public ViewConfiguration getUserViewConfig();

	public Probe[] getProbesConfig(boolean onlyDisplayable);
	public Probe[] getProbesConfig();

	public Map<String,ParameterGroup>  getParametersGroupConfig();

	public FilePath fileUpload(InputStream is, String fileName, String basePath);

	public UserDetails getCurrentUser();

	public Set<String> getPagesToNavigate();

	public void setChannelFavourite(Long id, boolean isFavourite);
}
