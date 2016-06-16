	@RequestMapping("/upload")
	public @ResponseBody Map<String, String> upload(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, String> resultMap = new HashMap<String, String>();
		//创建工具
		FileItemFactory factory = new DiskFileItemFactory();
		/*
		 * 创建解析器对象
		 */
		ServletFileUpload sfu = new ServletFileUpload(factory);
		sfu.setFileSizeMax(1024 * 1024); //设置单个上传文件的大小上限
		/*
		 * 解析request得到List<FileItem>
		 */
		List<FileItem> fileItemList = null;
		List<FileItem> photoItemList = new ArrayList<FileItem>();
		try {
			fileItemList = sfu.parseRequest(request);
		} catch (FileUploadException e) {
			logger.error("单个上传文件操作1M", e);
			resultMap.put("E0030", Const.E0030);
			return resultMap;
		}

		/*
		 * 获取其中的图片
		 */
		Iterator<FileItem> iter = fileItemList.iterator();
		while (iter.hasNext()) {
			FileItem item = iter.next();
			if (!item.isFormField()) {
				photoItemList.add(item);
			}
		}

		/*
		 * 如果没有文件
		 */
		if (null == photoItemList || photoItemList.isEmpty()) {
			logger.info("不存在该文件");
			resultMap.put("E0033", Const.E0033);
			return resultMap;
		}
		// 获取文件名
		FileItem fileItem = photoItemList.get(0);
		//文件标识
		logger.info("file field name ---- " + fileItem.getFieldName());
		String filename = fileItem.getName();
		// 截取文件名，因为部分浏览器上传的绝对路径
		int index = filename.lastIndexOf("\\");
		if (index != -1) {
			filename = filename.substring(index + 1);
		}
		// 给文件名添加uuid前缀，避免文件同名现象
		String uuid = UUID.randomUUID().toString();
		filename = uuid + "_" + filename;
		// 校验文件名称的扩展名
		if (!filename.toLowerCase().endsWith(".jpg")) {
			logger.info("上传的图片扩展名必须是JPG");
			resultMap.put("E0033", Const.E0033);
			return resultMap;
		}
		/*
		 * 保存图片：
		 * 1. 获取真实路径
		 */
		String savepath = uploadMap.get("upload_address");

		/*
		 * 2. 创建目标文件
		 */
		File destFile = new File(savepath, filename);
		/*
		 * 3. 保存文件
		 */
		try {
			fileItem.write(destFile);//它会把临时文件重定向到指定的路径，再删除临时文件
		} catch (Exception e) {
			logger.error("上传文件失败", e);
			resultMap.put("E0031", Const.E0031);
			return resultMap;
		}

		resultMap.put("S0032", Const.S0032);
		return resultMap;
	}